import React, { useState, useEffect } from 'react';
import { Link, useOutletContext } from 'react-router-dom';
import {
  Users,
  CheckCircle2,
  Clock,
  AlertTriangle,
  DollarSign,
  TrendingUp,
  Percent,
  Plus,
  ArrowUpRight,
  Sparkles,
  Calendar,
  Layers,
  ChevronRight
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  Legend
} from 'recharts';
import { dashboardApi } from '../api/dashboardApi';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import ScoreBadge from '../components/ScoreBadge';

const SOURCE_COLORS = ['#6366F1', '#8B5CF6', '#EC4899', '#3B82F6', '#10B981', '#F59E0B', '#64748B'];

export default function DashboardPage() {
  const { openNewLeadModal } = useOutletContext() || {};
  const [summary, setSummary] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      const [sumRes, anaRes] = await Promise.all([
        dashboardApi.getSummary(),
        dashboardApi.getAnalytics()
      ]);
      if (sumRes.success) setSummary(sumRes.data);
      if (anaRes.success) setAnalytics(anaRes.data);
    } catch (err) {
      console.error('Failed to load dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();

    // Listen to global lead-created event to refresh dashboard in real time
    const handleLeadChange = () => fetchData();
    window.addEventListener('lead-created', handleLeadChange);
    return () => window.removeEventListener('lead-created', handleLeadChange);
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="flex flex-col items-center space-y-3">
          <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-semibold text-slate-500">Loading CRM analytics...</p>
        </div>
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
      {/* Top Banner & Quick Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Executive Dashboard</h1>
          <p className="text-xs text-slate-500 mt-0.5">Real-time performance metrics, pipeline health, and sales telemetry.</p>
        </div>
        <div className="flex items-center space-x-2">
          <Link
            to="/ai-assistant"
            className="flex items-center space-x-1.5 px-3 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>AI Sales Copilot</span>
          </Link>
          <button
            onClick={openNewLeadModal}
            className="flex items-center space-x-1.5 px-3 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span>Add Lead</span>
          </button>
        </div>
      </div>

      {/* Overdue Alert Banner if any */}
      {(summary?.overdueFollowUps > 0 || summary?.followUpsDueToday > 0) && (
        <div className="p-4 bg-gradient-to-r from-amber-500/10 via-rose-500/10 to-indigo-500/10 border border-amber-200/60 rounded-2xl flex flex-col sm:flex-row sm:items-center justify-between gap-3 shadow-sm">
          <div className="flex items-center space-x-3">
            <div className="w-9 h-9 rounded-xl bg-amber-500/20 text-amber-700 flex items-center justify-center shrink-0">
              <Clock className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-xs font-bold text-slate-800">Follow-up Tasks Requiring Action</h4>
              <p className="text-xs text-slate-600">
                You have <span className="font-bold text-rose-600">{summary?.overdueFollowUps} overdue</span> and{' '}
                <span className="font-bold text-amber-600">{summary?.followUpsDueToday} due today</span>.
              </p>
            </div>
          </div>
          <Link
            to="/follow-ups"
            className="inline-flex items-center space-x-1 text-xs font-bold text-indigo-600 hover:text-indigo-800 shrink-0 self-end sm:self-auto"
          >
            <span>Open Follow-ups</span>
            <ChevronRight className="w-4 h-4" />
          </Link>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Leads */}
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Total Leads</p>
            <h3 className="text-2xl font-black text-slate-900 mt-1">{summary?.totalLeads}</h3>
            <span className="text-[11px] font-medium text-indigo-600 mt-1 inline-flex items-center">
              <span className="w-1.5 h-1.5 rounded-full bg-indigo-600 mr-1.5"></span>
              {summary?.newLeads} New Inbounds
            </span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <Users className="w-6 h-6" />
          </div>
        </div>

        {/* Pipeline Value */}
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Pipeline Value</p>
            <h3 className="text-2xl font-black text-slate-900 mt-1">{formatCurrency(summary?.totalPipelineValue)}</h3>
            <span className="text-[11px] font-medium text-purple-600 mt-1 inline-flex items-center">
              Active Negotiated Deals
            </span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-purple-50 text-purple-600 flex items-center justify-center">
            <DollarSign className="w-6 h-6" />
          </div>
        </div>

        {/* Converted Revenue */}
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Won Revenue</p>
            <h3 className="text-2xl font-black text-emerald-600 mt-1">{formatCurrency(summary?.convertedRevenue)}</h3>
            <span className="text-[11px] font-medium text-emerald-600 mt-1 inline-flex items-center">
              <CheckCircle2 className="w-3 h-3 mr-1" />
              {summary?.convertedLeads} Won Deals
            </span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <TrendingUp className="w-6 h-6" />
          </div>
        </div>

        {/* Conversion Rate */}
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">Conversion Rate</p>
            <h3 className="text-2xl font-black text-slate-900 mt-1">{summary?.conversionRate}%</h3>
            <span className="text-[11px] font-medium text-slate-500 mt-1 inline-flex items-center">
              {summary?.qualifiedLeads} Qualified
            </span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-teal-50 text-teal-600 flex items-center justify-center">
            <Percent className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Charts Section: Monthly Trends & Pipeline Stages */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Monthly Generation Chart */}
        <div className="lg:col-span-2 p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-800">Monthly Lead Generation & Revenue</h3>
              <p className="text-xs text-slate-400">Acquisition trajectory over the last 6 months</p>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-slate-100 text-[11px] font-semibold text-slate-600">
              Live Trend
            </span>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={analytics?.monthlyTrends || []} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorLeads" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366F1" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#6366F1" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#F1F5F9" />
                <XAxis dataKey="month" tick={{ fontSize: 11, fill: '#64748B' }} />
                <YAxis tick={{ fontSize: 11, fill: '#64748B' }} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#0B132B', borderColor: '#1E293B', borderRadius: '12px', color: '#fff', fontSize: '12px' }}
                />
                <Area type="monotone" dataKey="leads" stroke="#6366F1" strokeWidth={2.5} fillOpacity={1} fill="url(#colorLeads)" name="Leads" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Lead Sources Distribution Donut */}
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col">
          <div className="mb-2">
            <h3 className="text-sm font-bold text-slate-800">Lead Sources</h3>
            <p className="text-xs text-slate-400">Channel origin breakdown</p>
          </div>
          <div className="h-60 w-full flex-1">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={analytics?.sourceDistribution || []}
                  dataKey="count"
                  nameKey="source"
                  cx="50%"
                  cy="50%"
                  innerRadius={50}
                  outerRadius={75}
                  paddingAngle={4}
                >
                  {(analytics?.sourceDistribution || []).map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={SOURCE_COLORS[index % SOURCE_COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{ backgroundColor: '#0B132B', borderColor: '#1E293B', borderRadius: '12px', color: '#fff', fontSize: '12px' }}
                />
                <Legend iconType="circle" wrapperStyle={{ fontSize: '11px', paddingTop: '10px' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Pipeline Stage Funnel Bar Chart */}
      <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-sm font-bold text-slate-800">Sales Pipeline Stages</h3>
            <p className="text-xs text-slate-400">Total volume and progression across stages</p>
          </div>
          <Link to="/pipeline" className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center space-x-1">
            <span>View Kanban Board</span>
            <ArrowUpRight className="w-3.5 h-3.5" />
          </Link>
        </div>
        <div className="h-56 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={analytics?.stageFunnel || []} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#F1F5F9" />
              <XAxis dataKey="stage" tick={{ fontSize: 10, fill: '#64748B' }} />
              <YAxis tick={{ fontSize: 11, fill: '#64748B' }} />
              <Tooltip
                contentStyle={{ backgroundColor: '#0B132B', borderColor: '#1E293B', borderRadius: '12px', color: '#fff', fontSize: '12px' }}
              />
              <Bar dataKey="count" fill="#8B5CF6" radius={[6, 6, 0, 0]} name="Lead Count" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Bottom Grid: Recent Leads & Recent Activities Feed */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Leads Table */}
        <div className="lg:col-span-2 p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-800">Recently Added Leads</h3>
              <p className="text-xs text-slate-400">Latest business prospects in the CRM</p>
            </div>
            <Link to="/leads" className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center space-x-1">
              <span>All Leads</span>
              <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-100 text-slate-400 font-semibold uppercase tracking-wider">
                  <th className="pb-3">Lead</th>
                  <th className="pb-3">Deal Value</th>
                  <th className="pb-3">Stage</th>
                  <th className="pb-3">AI Score</th>
                  <th className="pb-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {(summary?.recentLeads || []).map((lead) => (
                  <tr key={lead.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-3">
                      <Link to={`/leads/${lead.id}`} className="font-semibold text-slate-900 hover:text-indigo-600">
                        {lead.fullName}
                      </Link>
                      <span className="block text-[11px] text-slate-400">{lead.company}</span>
                    </td>
                    <td className="py-3 font-semibold text-slate-800">
                      {formatCurrency(lead.estimatedDealValue)}
                    </td>
                    <td className="py-3">
                      <StatusBadge status={lead.status} />
                    </td>
                    <td className="py-3">
                      <ScoreBadge score={lead.aiScore} />
                    </td>
                    <td className="py-3 text-right">
                      <Link
                        to={`/leads/${lead.id}`}
                        className="px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium text-[11px] transition"
                      >
                        Details
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Recent Activities Feed */}
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-slate-800">Recent Sales Activities</h3>
              <p className="text-xs text-slate-400">Calls, emails & meetings logged</p>
            </div>
            <Calendar className="w-4 h-4 text-slate-400" />
          </div>
          <div className="space-y-3.5 max-h-[320px] overflow-y-auto pr-1">
            {(summary?.recentActivities || []).map((act) => (
              <div key={act.id} className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs space-y-1">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-indigo-700 uppercase text-[10px] bg-indigo-50 px-2 py-0.5 rounded">
                    {act.type}
                  </span>
                  <span className="text-[10px] text-slate-400">
                    {new Date(act.activityTime).toLocaleDateString()}
                  </span>
                </div>
                <p className="font-semibold text-slate-800">{act.title}</p>
                {act.description && (
                  <p className="text-slate-500 text-[11px] line-clamp-2">{act.description}</p>
                )}
                {act.leadName && (
                  <p className="text-[10px] text-slate-400 font-medium pt-1">Lead: {act.leadName}</p>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
