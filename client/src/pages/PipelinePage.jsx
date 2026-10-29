import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  KanbanSquare,
  DollarSign,
  Plus,
  ArrowRight,
  ArrowLeft,
  Sparkles,
  ChevronRight,
  ChevronLeft
} from 'lucide-react';
import { leadApi } from '../api/leadApi';
import PriorityBadge from '../components/PriorityBadge';
import ScoreBadge from '../components/ScoreBadge';
import LeadModal from '../components/LeadModal';

const COLUMNS = [
  { id: 'NEW', label: 'New Inbound', color: 'border-t-blue-500' },
  { id: 'CONTACTED', label: 'Contacted', color: 'border-t-purple-500' },
  { id: 'QUALIFIED', label: 'Qualified', color: 'border-t-indigo-500' },
  { id: 'PROPOSAL', label: 'Proposal', color: 'border-t-amber-500' },
  { id: 'NEGOTIATION', label: 'Negotiation', color: 'border-t-orange-500' },
  { id: 'CONVERTED', label: 'Won / Converted', color: 'border-t-emerald-500' },
  { id: 'LOST', label: 'Lost', color: 'border-t-slate-400' },
];

export default function PipelinePage() {
  const [leads, setLeads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);

  const fetchLeads = async () => {
    try {
      const res = await leadApi.getAllLeads();
      if (res.success && res.data) {
        setLeads(res.data);
      }
    } catch (err) {
      console.error('Failed to load pipeline leads:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeads();
  }, []);

  const handleMoveStage = async (leadId, targetStage) => {
    try {
      // Optimistic update
      setLeads(prev => prev.map(l => l.id === leadId ? { ...l, status: targetStage } : l));
      await leadApi.updateLeadStatus(leadId, targetStage, `Moved via Pipeline Kanban to ${targetStage}`);
      window.dispatchEvent(new CustomEvent('lead-created'));
    } catch (err) {
      alert('Failed to update stage');
      fetchLeads();
    }
  };

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(val || 0);
  };

  const getStageTotal = (stageId) => {
    return leads
      .filter(l => l.status === stageId)
      .reduce((sum, l) => sum + (Number(l.estimatedDealValue) || 0), 0);
  };

  return (
    <div className="space-y-5 animate-in fade-in duration-150">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Sales Pipeline</h1>
          <p className="text-xs text-slate-500 mt-0.5">Interactive Kanban visualizer. Move deals across qualification stages.</p>
        </div>
        <button
          onClick={() => setModalOpen(true)}
          className="flex items-center space-x-1.5 px-3.5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
        >
          <Plus className="w-4 h-4" />
          <span>New Lead</span>
        </button>
      </div>

      {/* Kanban Board Horizontal Scroll Container */}
      {loading ? (
        <div className="flex items-center justify-center min-h-[50vh]">
          <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
        </div>
      ) : (
        <div className="flex gap-4 overflow-x-auto pb-6 pt-1 snap-x min-h-[calc(100vh-220px)]">
          {COLUMNS.map((col, colIdx) => {
            const colLeads = leads.filter(l => l.status === col.id);
            const totalVal = getStageTotal(col.id);

            return (
              <div
                key={col.id}
                className={`w-72 shrink-0 bg-slate-100/70 rounded-2xl border border-slate-200/80 border-t-4 ${col.color} p-3 flex flex-col`}
              >
                {/* Column Header */}
                <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-200/60">
                  <div>
                    <h3 className="text-xs font-bold text-slate-800">{col.label}</h3>
                    <span className="text-[11px] font-semibold text-slate-500">
                      {formatCurrency(totalVal)}
                    </span>
                  </div>
                  <span className="w-5 h-5 rounded-full bg-white border border-slate-200 text-[10px] font-bold text-slate-700 flex items-center justify-center shadow-xs">
                    {colLeads.length}
                  </span>
                </div>

                {/* Cards Container */}
                <div className="space-y-2.5 overflow-y-auto flex-1 pr-0.5 max-h-[70vh]">
                  {colLeads.length === 0 ? (
                    <div className="h-28 border border-dashed border-slate-200 rounded-xl flex items-center justify-center text-slate-400 text-xs">
                      No deals in this stage
                    </div>
                  ) : (
                    colLeads.map((lead) => (
                      <div
                        key={lead.id}
                        className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm hover:shadow-md transition space-y-2.5 group"
                      >
                        <div className="flex items-start justify-between gap-1">
                          <div>
                            <Link
                              to={`/leads/${lead.id}`}
                              className="font-bold text-xs text-slate-900 group-hover:text-indigo-600 transition block"
                            >
                              {lead.fullName}
                            </Link>
                            <span className="text-[11px] text-slate-500 block">{lead.company}</span>
                          </div>
                          <ScoreBadge score={lead.aiScore} />
                        </div>

                        <div className="flex items-center justify-between text-xs pt-1 border-t border-slate-100">
                          <span className="font-bold text-slate-900">
                            {formatCurrency(lead.estimatedDealValue)}
                          </span>
                          <PriorityBadge priority={lead.priority} />
                        </div>

                        {/* Stage Mover Arrows */}
                        <div className="flex items-center justify-between pt-1 border-t border-slate-50 text-[10px] text-slate-400">
                          {colIdx > 0 ? (
                            <button
                              onClick={() => handleMoveStage(lead.id, COLUMNS[colIdx - 1].id)}
                              title={`Move back to ${COLUMNS[colIdx - 1].label}`}
                              className="flex items-center space-x-1 hover:text-slate-800 p-0.5 rounded"
                            >
                              <ChevronLeft className="w-3.5 h-3.5" />
                              <span>Back</span>
                            </button>
                          ) : <div></div>}

                          {colIdx < COLUMNS.length - 1 && (
                            <button
                              onClick={() => handleMoveStage(lead.id, COLUMNS[colIdx + 1].id)}
                              title={`Advance to ${COLUMNS[colIdx + 1].label}`}
                              className="flex items-center space-x-1 hover:text-indigo-600 font-semibold p-0.5 rounded text-indigo-600"
                            >
                              <span>Advance</span>
                              <ChevronRight className="w-3.5 h-3.5" />
                            </button>
                          )}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* New Lead Modal */}
      {modalOpen && (
        <LeadModal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          onSuccess={() => {
            setModalOpen(false);
            fetchLeads();
          }}
        />
      )}
    </div>
  );
}
