import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  ArrowLeft,
  Building,
  Mail,
  Phone,
  Briefcase,
  DollarSign,
  Sparkles,
  MessageSquare,
  Plus,
  Clock,
  CheckCircle2,
  Calendar,
  Layers,
  History,
  FileText,
  AlertCircle
} from 'lucide-react';
import { leadApi } from '../api/leadApi';
import { activityApi } from '../api/activityApi';
import { followUpApi } from '../api/followUpApi';
import { aiApi } from '../api/aiApi';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import ScoreBadge from '../components/ScoreBadge';
import ActivityModal from '../components/ActivityModal';
import FollowUpModal from '../components/FollowUpModal';
import AiFollowUpModal from '../components/AiFollowUpModal';
import LeadModal from '../components/LeadModal';

const STAGES = ['NEW', 'CONTACTED', 'QUALIFIED', 'PROPOSAL', 'NEGOTIATION', 'CONVERTED', 'LOST'];

export default function LeadDetailPage() {
  const { id } = useParams();
  const [lead, setLead] = useState(null);
  const [activities, setActivities] = useState([]);
  const [followUps, setFollowUps] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  // AI States
  const [aiScoring, setAiScoring] = useState(false);
  const [aiSummary, setAiSummary] = useState(null);
  const [aiSummaryLoading, setAiSummaryLoading] = useState(false);

  // Modals
  const [activityModalOpen, setActivityModalOpen] = useState(false);
  const [followUpModalOpen, setFollowUpModalOpen] = useState(false);
  const [aiOutreachModalOpen, setAiOutreachModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);

  const loadData = async () => {
    try {
      const [leadRes, actRes, folRes, histRes] = await Promise.all([
        leadApi.getLeadById(id),
        activityApi.getActivitiesByLead(id),
        followUpApi.getFollowUps('ALL', id),
        leadApi.getLeadHistory(id),
      ]);

      if (leadRes.success) setLead(leadRes.data);
      if (actRes.success) setActivities(actRes.data);
      if (folRes.success) setFollowUps(actRes.data ? folRes.data : []);
      if (histRes.success) setHistory(histRes.data);
    } catch (err) {
      console.error('Failed to load lead details:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const handleStageChange = async (newStatus) => {
    try {
      await leadApi.updateLeadStatus(id, newStatus, `Stage changed to ${newStatus}`);
      loadData();
    } catch (err) {
      alert('Failed to update stage: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleAiScore = async () => {
    setAiScoring(true);
    try {
      const res = await aiApi.scoreLead(id);
      if (res.success && res.data) {
        setLead(prev => ({
          ...prev,
          aiScore: res.data.score,
          aiScoreExplanation: res.data.explanation,
          aiRecommendedAction: res.data.suggestedNextAction,
        }));
      }
    } catch (err) {
      alert('AI Scoring failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setAiScoring(false);
    }
  };

  const handleAiSummary = async () => {
    setAiSummaryLoading(true);
    try {
      const res = await aiApi.generateLeadSummary(id);
      if (res.success && res.data) {
        setAiSummary(res.data);
      }
    } catch (err) {
      alert('AI Summary failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setAiSummaryLoading(false);
    }
  };

  const handleCompleteFollowUp = async (followUpId) => {
    try {
      await followUpApi.completeFollowUp(followUpId);
      loadData();
    } catch (err) {
      alert('Failed to complete follow up');
    }
  };

  if (loading || !lead) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(val || 0);
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-150">
      {/* Top Navigation & Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="flex items-center space-x-3">
          <Link
            to="/leads"
            className="p-2 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-600 transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </Link>
          <div>
            <div className="flex items-center space-x-2">
              <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">{lead.fullName}</h1>
              <StatusBadge status={lead.status} />
              <PriorityBadge priority={lead.priority} />
            </div>
            <p className="text-xs text-slate-500 mt-0.5">{lead.jobTitle ? `${lead.jobTitle} at ` : ''}{lead.company}</p>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center space-x-2">
          <button
            onClick={() => setAiOutreachModalOpen(true)}
            className="flex items-center space-x-1.5 px-3 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>AI Draft Outreach</span>
          </button>
          <button
            onClick={() => setActivityModalOpen(true)}
            className="flex items-center space-x-1.5 px-3 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Log Activity</span>
          </button>
          <button
            onClick={() => setEditModalOpen(true)}
            className="px-3 py-2 border border-slate-300 bg-white hover:bg-slate-50 text-slate-700 rounded-xl text-xs font-semibold shadow-sm transition"
          >
            Edit
          </button>
        </div>
      </div>

      {/* Main Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column (2 cols): Details, AI Scoring, Activities & Tasks */}
        <div className="lg:col-span-2 space-y-6">
          {/* Quick Stage Progression Bar */}
          <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-2">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block">Pipeline Stage Progression</span>
            <div className="flex flex-wrap gap-1.5">
              {STAGES.map((st) => {
                const isCurrent = lead.status === st;
                return (
                  <button
                    key={st}
                    onClick={() => handleStageChange(st)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                      isCurrent
                        ? 'bg-indigo-600 text-white shadow-sm'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {st}
                  </button>
                );
              })}
            </div>
          </div>

          {/* AI Lead Scoring Card */}
          <div className="p-5 bg-gradient-to-br from-purple-900/10 via-indigo-900/5 to-white rounded-2xl border border-purple-200 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div className="w-8 h-8 rounded-xl bg-purple-600 text-white flex items-center justify-center shadow-sm">
                  <Sparkles className="w-4 h-4" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-slate-900">AI Predictive Lead Scoring</h3>
                  <span className="text-[11px] text-purple-700 font-medium">Powered by Enterprise ML Engine</span>
                </div>
              </div>
              <button
                onClick={handleAiScore}
                disabled={aiScoring}
                className="flex items-center space-x-1.5 px-3 py-1.5 bg-purple-600 hover:bg-purple-700 disabled:opacity-50 text-white rounded-xl text-xs font-semibold shadow transition active:scale-95"
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>{aiScoring ? 'Calculating...' : 'Recalculate AI Score'}</span>
              </button>
            </div>

            <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4 pt-2">
              <div className="flex items-center space-x-3">
                <div className={`w-16 h-16 rounded-2xl flex items-center justify-center font-black text-2xl shadow-inner border ${
                  (lead.aiScore || 0) >= 75 ? 'bg-emerald-50 text-emerald-700 border-emerald-300' :
                  (lead.aiScore || 0) >= 50 ? 'bg-indigo-50 text-indigo-700 border-indigo-300' :
                  'bg-amber-50 text-amber-700 border-amber-300'
                }`}>
                  {lead.aiScore !== null ? lead.aiScore : '--'}
                </div>
                <div>
                  <span className="text-xs font-bold text-slate-800 block">
                    {lead.aiScore !== null ? 'Propensity Score (0-100)' : 'Unscored Lead'}
                  </span>
                  <span className="text-[11px] text-slate-500 block">
                    {lead.aiScoreExplanation || 'Click Recalculate to generate AI evaluation for this deal.'}
                  </span>
                </div>
              </div>
            </div>

            {lead.aiRecommendedAction && (
              <div className="p-3 bg-white/80 border border-purple-100 rounded-xl text-xs text-slate-700">
                <span className="font-bold text-purple-900 block mb-0.5">Recommended Next Action:</span>
                <p>{lead.aiRecommendedAction}</p>
              </div>
            )}
          </div>

          {/* AI Executive Summary Widget */}
          <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <FileText className="w-4 h-4 text-indigo-600" />
                <h3 className="text-sm font-bold text-slate-800">Executive AI Summary</h3>
              </div>
              <button
                onClick={handleAiSummary}
                disabled={aiSummaryLoading}
                className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center space-x-1"
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>{aiSummaryLoading ? 'Generating...' : aiSummary ? 'Regenerate' : 'Generate AI Brief'}</span>
              </button>
            </div>

            {aiSummary ? (
              <div className="space-y-3 text-xs animate-in fade-in">
                <p className="text-slate-700 font-medium leading-relaxed bg-slate-50 p-3 rounded-xl border border-slate-100">
                  {aiSummary.summary}
                </p>
                {aiSummary.keyHighlights?.length > 0 && (
                  <div>
                    <span className="font-bold text-slate-800 block mb-1">Key Highlights:</span>
                    <ul className="list-disc list-inside space-y-0.5 text-slate-600 pl-1">
                      {aiSummary.keyHighlights.map((h, i) => <li key={i}>{h}</li>)}
                    </ul>
                  </div>
                )}
                {aiSummary.recommendedNextSteps?.length > 0 && (
                  <div>
                    <span className="font-bold text-slate-800 block mb-1">Strategic Next Steps:</span>
                    <ul className="list-disc list-inside space-y-0.5 text-slate-600 pl-1">
                      {aiSummary.recommendedNextSteps.map((s, i) => <li key={i}>{s}</li>)}
                    </ul>
                  </div>
                )}
              </div>
            ) : (
              <p className="text-xs text-slate-500 italic">
                Generate a 30-second executive summary synthesized from lead communications and pipeline metrics.
              </p>
            )}
          </div>

          {/* Activities Timeline */}
          <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-sm font-bold text-slate-800">Activity History</h3>
                <p className="text-xs text-slate-400">Calls, emails, meetings, and client updates</p>
              </div>
              <button
                onClick={() => setActivityModalOpen(true)}
                className="flex items-center space-x-1 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-semibold transition"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Log Touchpoint</span>
              </button>
            </div>

            {activities.length === 0 ? (
              <div className="py-8 text-center text-slate-400 text-xs">
                No activities logged yet. Click "Log Touchpoint" to add the first interaction.
              </div>
            ) : (
              <div className="relative pl-6 space-y-4 before:content-[''] before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-200">
                {activities.map((act) => (
                  <div key={act.id} className="relative text-xs space-y-1">
                    <span className="absolute -left-6 top-1 w-2.5 h-2.5 rounded-full bg-indigo-600 ring-4 ring-white"></span>
                    <div className="flex items-center space-x-2">
                      <span className="font-bold text-[10px] uppercase bg-indigo-50 text-indigo-700 px-2 py-0.5 rounded">
                        {act.type}
                      </span>
                      <span className="font-semibold text-slate-800">{act.title}</span>
                      <span className="text-[10px] text-slate-400">
                        {new Date(act.activityTime).toLocaleString()}
                      </span>
                    </div>
                    {act.description && (
                      <p className="text-slate-600 bg-slate-50 p-2.5 rounded-xl border border-slate-100 whitespace-pre-wrap">
                        {act.description}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Lead Metadata, Scheduled Follow-ups, Status History */}
        <div className="space-y-6">
          {/* Metadata Overview Card */}
          <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-4">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Lead Profile</h3>
            <div className="space-y-3 text-xs">
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-slate-400">Estimated Deal</span>
                <span className="font-bold text-slate-900 text-sm">{formatCurrency(lead.estimatedDealValue)}</span>
              </div>
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-slate-400">Company</span>
                <span className="font-semibold text-slate-800">{lead.company}</span>
              </div>
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-slate-400">Email</span>
                <a href={`mailto:${lead.email}`} className="font-medium text-indigo-600 hover:underline">{lead.email}</a>
              </div>
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-slate-400">Phone</span>
                <span className="font-medium text-slate-700">{lead.phone || 'N/A'}</span>
              </div>
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-slate-400">Industry</span>
                <span className="font-medium text-slate-700">{lead.industry || 'General Business'}</span>
              </div>
              <div className="flex items-center justify-between pb-2 border-b border-slate-100">
                <span className="text-slate-400">Source</span>
                <span className="font-semibold text-slate-800">{lead.leadSource}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-slate-400">Assigned Rep</span>
                <span className="font-semibold text-slate-800">{lead.assignedTo?.fullName || 'Unassigned'}</span>
              </div>
            </div>

            {lead.notes && (
              <div className="pt-2 border-t border-slate-100">
                <span className="text-[11px] font-semibold text-slate-400 block mb-1">Notes:</span>
                <p className="text-xs text-slate-600 bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                  {lead.notes}
                </p>
              </div>
            )}
          </div>

          {/* Scheduled Follow-ups */}
          <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Scheduled Tasks</h3>
              </div>
              <button
                onClick={() => setFollowUpModalOpen(true)}
                className="p-1 rounded-lg text-indigo-600 hover:bg-indigo-50 font-semibold text-xs flex items-center space-x-1"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Add</span>
              </button>
            </div>

            {followUps.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No pending follow-ups scheduled.</p>
            ) : (
              <div className="space-y-2">
                {followUps.map(f => {
                  const isDone = f.status === 'COMPLETED';
                  return (
                    <div
                      key={f.id}
                      className={`p-3 rounded-xl border text-xs flex items-start justify-between gap-2 transition ${
                        isDone ? 'bg-slate-50/50 border-slate-100 opacity-60' : 'bg-white border-slate-200'
                      }`}
                    >
                      <div className="space-y-1">
                        <p className={`font-semibold ${isDone ? 'line-through text-slate-400' : 'text-slate-800'}`}>
                          {f.title}
                        </p>
                        <span className="text-[10px] text-slate-400 block">
                          Due: {new Date(f.dueDate).toLocaleString()}
                        </span>
                      </div>
                      {!isDone && (
                        <button
                          onClick={() => handleCompleteFollowUp(f.id)}
                          title="Mark complete"
                          className="p-1 text-slate-400 hover:text-emerald-600 rounded transition"
                        >
                          <CheckCircle2 className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Lifecycle Status Change History */}
          <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Status History</h3>
            <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
              {history.map((h) => (
                <div key={h.id} className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 text-xs space-y-0.5">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-slate-800">{h.oldStatus ? `${h.oldStatus} → ${h.newStatus}` : h.newStatus}</span>
                    <span className="text-[10px] text-slate-400">{new Date(h.createdAt).toLocaleDateString()}</span>
                  </div>
                  {h.notes && <p className="text-[11px] text-slate-500">{h.notes}</p>}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      {activityModalOpen && (
        <ActivityModal
          isOpen={activityModalOpen}
          leadId={id}
          onClose={() => setActivityModalOpen(false)}
          onSuccess={() => {
            setActivityModalOpen(false);
            loadData();
          }}
        />
      )}

      {followUpModalOpen && (
        <FollowUpModal
          isOpen={followUpModalOpen}
          leadId={id}
          onClose={() => setFollowUpModalOpen(false)}
          onSuccess={() => {
            setFollowUpModalOpen(false);
            loadData();
          }}
        />
      )}

      {aiOutreachModalOpen && (
        <AiFollowUpModal
          isOpen={aiOutreachModalOpen}
          lead={lead}
          onClose={() => setAiOutreachModalOpen(false)}
        />
      )}

      {editModalOpen && (
        <LeadModal
          isOpen={editModalOpen}
          initialLead={lead}
          onClose={() => setEditModalOpen(false)}
          onSuccess={() => {
            setEditModalOpen(false);
            loadData();
          }}
        />
      )}
    </div>
  );
}
