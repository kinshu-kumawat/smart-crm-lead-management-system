import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  CalendarCheck2,
  Clock,
  Plus,
  CheckCircle2,
  AlertTriangle,
  Building,
  User,
  Check
} from 'lucide-react';
import { followUpApi } from '../api/followUpApi';
import FollowUpModal from '../components/FollowUpModal';
import { leadApi } from '../api/leadApi';

const TABS = [
  { id: 'ALL', label: 'All Tasks' },
  { id: 'OVERDUE', label: 'Overdue' },
  { id: 'TODAY', label: 'Due Today' },
  { id: 'UPCOMING', label: 'Upcoming' },
  { id: 'COMPLETED', label: 'Completed' },
];

export default function FollowUpsPage() {
  const [tasks, setTasks] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [leads, setLeads] = useState([]);
  const [selectedLeadId, setSelectedLeadId] = useState(null);

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const res = await followUpApi.getFollowUps(filter);
      if (res.success && res.data) {
        setTasks(res.data);
      }
    } catch (err) {
      console.error('Failed to load follow-ups:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
    leadApi.getAllLeads().then(res => {
      if (res.success && res.data && res.data.length > 0) {
        setLeads(res.data);
        setSelectedLeadId(res.data[0].id);
      }
    }).catch(() => {});
  }, [filter]);

  const handleComplete = async (id) => {
    try {
      await followUpApi.completeFollowUp(id);
      fetchTasks();
      window.dispatchEvent(new CustomEvent('lead-created'));
    } catch (err) {
      alert('Failed to complete follow up');
    }
  };

  return (
    <div className="space-y-5 animate-in fade-in duration-150">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Follow-ups & Tasks</h1>
          <p className="text-xs text-slate-500 mt-0.5">Track upcoming meetings, scheduled outreach calls, and overdue client tasks.</p>
        </div>
        {leads.length > 0 && (
          <button
            onClick={() => setModalOpen(true)}
            className="flex items-center space-x-1.5 px-3.5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span>Schedule Follow-up</span>
          </button>
        )}
      </div>

      {/* Tabs */}
      <div className="flex items-center space-x-1 border-b border-slate-200">
        {TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => setFilter(tab.id)}
            className={`px-4 py-2.5 text-xs font-semibold border-b-2 transition ${
              filter === tab.id
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Task List */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-400 text-xs">
            <div className="w-6 h-6 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
            Loading follow-up tasks...
          </div>
        ) : tasks.length === 0 ? (
          <div className="p-12 text-center text-slate-400 text-xs">
            <CalendarCheck2 className="w-10 h-10 text-slate-300 mx-auto mb-2" />
            <p className="font-semibold text-slate-600">No follow-ups in this category</p>
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {tasks.map(task => {
              const isCompleted = task.status === 'COMPLETED';
              return (
                <div
                  key={task.id}
                  className={`p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-slate-50/80 transition ${
                    task.overdue ? 'bg-rose-50/30' : ''
                  }`}
                >
                  <div className="flex items-start space-x-3">
                    <button
                      onClick={() => !isCompleted && handleComplete(task.id)}
                      disabled={isCompleted}
                      className={`mt-0.5 w-5 h-5 rounded-lg border flex items-center justify-center transition shrink-0 ${
                        isCompleted
                          ? 'bg-emerald-600 border-emerald-600 text-white'
                          : 'border-slate-300 hover:border-indigo-600 text-transparent hover:text-indigo-600'
                      }`}
                    >
                      <Check className="w-3.5 h-3.5 stroke-[3]" />
                    </button>

                    <div>
                      <h4 className={`text-xs font-bold ${isCompleted ? 'line-through text-slate-400' : 'text-slate-900'}`}>
                        {task.title}
                      </h4>
                      {task.description && (
                        <p className="text-[11px] text-slate-500 mt-0.5">{task.description}</p>
                      )}
                      <div className="flex flex-wrap items-center gap-3 mt-1 text-[11px] text-slate-400 font-medium">
                        {task.leadName && (
                          <Link to={`/leads/${task.leadId}`} className="text-indigo-600 hover:underline flex items-center space-x-1">
                            <Building className="w-3 h-3" />
                            <span>{task.leadName} ({task.leadCompany})</span>
                          </Link>
                        )}
                        <span className="flex items-center space-x-1">
                          <User className="w-3 h-3" />
                          <span>{task.assignedTo?.fullName || 'Unassigned'}</span>
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Due Date & Badges */}
                  <div className="flex items-center space-x-2 shrink-0 self-end sm:self-center">
                    {task.overdue && (
                      <span className="px-2 py-0.5 bg-rose-100 text-rose-700 text-[10px] font-bold rounded-full flex items-center space-x-1">
                        <AlertTriangle className="w-3 h-3" />
                        <span>OVERDUE</span>
                      </span>
                    )}
                    <span className="text-xs font-medium text-slate-600 bg-slate-100 px-2.5 py-1 rounded-lg">
                      {new Date(task.dueDate).toLocaleString()}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {modalOpen && selectedLeadId && (
        <FollowUpModal
          isOpen={modalOpen}
          leadId={selectedLeadId}
          onClose={() => setModalOpen(false)}
          onSuccess={() => {
            setModalOpen(false);
            fetchTasks();
          }}
        />
      )}
    </div>
  );
}
