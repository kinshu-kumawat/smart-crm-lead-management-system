import React, { useState, useEffect } from 'react';
import { Link, useOutletContext } from 'react-router-dom';
import {
  Users,
  Search,
  Filter,
  Download,
  Plus,
  ArrowUpDown,
  MoreVertical,
  Edit2,
  Trash2,
  Eye,
  ChevronLeft,
  ChevronRight,
  Sparkles,
  AlertCircle
} from 'lucide-react';
import { leadApi } from '../api/leadApi';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import ScoreBadge from '../components/ScoreBadge';
import LeadModal from '../components/LeadModal';

const STATUS_OPTIONS = ['ALL', 'NEW', 'CONTACTED', 'QUALIFIED', 'PROPOSAL', 'NEGOTIATION', 'CONVERTED', 'LOST'];
const PRIORITY_OPTIONS = ['ALL', 'LOW', 'MEDIUM', 'HIGH'];
const SOURCE_OPTIONS = ['ALL', 'WEBSITE', 'LINKEDIN', 'REFERRAL', 'EMAIL', 'PHONE', 'AD_CAMPAIGN', 'OTHER'];

export default function LeadsPage() {
  const [leads, setLeads] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [priority, setPriority] = useState('ALL');
  const [source, setSource] = useState('ALL');
  const [sortBy, setSortBy] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [selectedLead, setSelectedLead] = useState(null);
  const [deleteConfirmId, setDeleteConfirmId] = useState(null);

  const fetchLeads = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size,
        sortBy,
        sortDir,
      };
      if (search.trim()) params.search = search.trim();
      if (status !== 'ALL') params.status = status;
      if (priority !== 'ALL') params.priority = priority;
      if (source !== 'ALL') params.source = source;

      const res = await leadApi.getLeads(params);
      if (res.success && res.data) {
        setLeads(res.data.content);
        setTotalElements(res.data.totalElements);
        setTotalPages(res.data.totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load leads:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeads();
  }, [page, size, status, priority, source, sortBy, sortDir]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchLeads();
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortDir('asc');
    }
  };

  const handleDelete = async (id) => {
    try {
      await leadApi.deleteLead(id);
      setDeleteConfirmId(null);
      fetchLeads();
    } catch (err) {
      alert('Failed to delete lead: ' + (err.response?.data?.message || err.message));
    }
  };

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(val || 0);
  };

  return (
    <div className="space-y-5 animate-in fade-in duration-150">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">Lead Management</h1>
          <p className="text-xs text-slate-500 mt-0.5">Manage, track, and score your prospective enterprise clients.</p>
        </div>
        <div className="flex items-center space-x-2">
          <a
            href={leadApi.exportCsvUrl()}
            download
            className="flex items-center space-x-1.5 px-3 py-2 border border-slate-300 bg-white hover:bg-slate-50 text-slate-700 rounded-xl text-xs font-semibold shadow-sm transition"
          >
            <Download className="w-4 h-4 text-slate-500" />
            <span>Export CSV</span>
          </a>
          <button
            onClick={() => {
              setSelectedLead(null);
              setModalOpen(true);
            }}
            className="flex items-center space-x-1.5 px-3.5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span>Add New Lead</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-3">
        <div className="flex flex-col md:flex-row items-center gap-3">
          <form onSubmit={handleSearchSubmit} className="relative flex-1 w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by name, company, email, job title..."
              className="w-full pl-10 pr-20 py-2 border border-slate-300 rounded-xl text-xs focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
            />
            <button
              type="submit"
              className="absolute right-2 top-1.5 px-2.5 py-1 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-[11px] font-semibold transition"
            >
              Search
            </button>
          </form>

          {/* Filters */}
          <div className="grid grid-cols-3 gap-2 w-full md:w-auto">
            {/* Status */}
            <select
              value={status}
              onChange={(e) => { setStatus(e.target.value); setPage(0); }}
              className="px-3 py-2 border border-slate-300 rounded-xl text-xs bg-white text-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none"
            >
              <option value="ALL">All Stages</option>
              {STATUS_OPTIONS.filter(s => s !== 'ALL').map(s => <option key={s} value={s}>{s}</option>)}
            </select>

            {/* Priority */}
            <select
              value={priority}
              onChange={(e) => { setPriority(e.target.value); setPage(0); }}
              className="px-3 py-2 border border-slate-300 rounded-xl text-xs bg-white text-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none"
            >
              <option value="ALL">All Priorities</option>
              {PRIORITY_OPTIONS.filter(p => p !== 'ALL').map(p => <option key={p} value={p}>{p}</option>)}
            </select>

            {/* Source */}
            <select
              value={source}
              onChange={(e) => { setSource(e.target.value); setPage(0); }}
              className="px-3 py-2 border border-slate-300 rounded-xl text-xs bg-white text-slate-700 focus:ring-2 focus:ring-indigo-500 outline-none"
            >
              <option value="ALL">All Sources</option>
              {SOURCE_OPTIONS.filter(s => s !== 'ALL').map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>
        </div>
      </div>

      {/* Leads Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
                <th className="py-3.5 px-4 cursor-pointer hover:text-slate-900" onClick={() => handleSort('fullName')}>
                  <div className="flex items-center space-x-1">
                    <span>Lead Name & Company</span>
                    <ArrowUpDown className="w-3 h-3" />
                  </div>
                </th>
                <th className="py-3.5 px-4">Contact</th>
                <th className="py-3.5 px-4 cursor-pointer hover:text-slate-900" onClick={() => handleSort('estimatedDealValue')}>
                  <div className="flex items-center space-x-1">
                    <span>Deal Value</span>
                    <ArrowUpDown className="w-3 h-3" />
                  </div>
                </th>
                <th className="py-3.5 px-4">Stage</th>
                <th className="py-3.5 px-4">Priority</th>
                <th className="py-3.5 px-4">AI Score</th>
                <th className="py-3.5 px-4">Assigned Rep</th>
                <th className="py-3.5 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="8" className="py-12 text-center text-slate-400">
                    <div className="w-6 h-6 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
                    Loading leads data...
                  </td>
                </tr>
              ) : leads.length === 0 ? (
                <tr>
                  <td colSpan="8" className="py-12 text-center text-slate-400">
                    <Users className="w-10 h-10 text-slate-300 mx-auto mb-2" />
                    <p className="font-semibold text-slate-600">No leads found</p>
                    <p className="text-xs mt-1">Try adjusting your filters or click "Add New Lead".</p>
                  </td>
                </tr>
              ) : (
                leads.map((lead) => (
                  <tr key={lead.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-3.5 px-4">
                      <Link to={`/leads/${lead.id}`} className="font-bold text-slate-900 hover:text-indigo-600 transition block">
                        {lead.fullName}
                      </Link>
                      <span className="text-slate-500 font-medium block">{lead.company}</span>
                      {lead.jobTitle && <span className="text-[11px] text-slate-400 block">{lead.jobTitle}</span>}
                    </td>
                    <td className="py-3.5 px-4">
                      <span className="text-slate-700 block">{lead.email}</span>
                      {lead.phone && <span className="text-slate-400 text-[11px] block">{lead.phone}</span>}
                    </td>
                    <td className="py-3.5 px-4 font-bold text-slate-900">
                      {formatCurrency(lead.estimatedDealValue)}
                    </td>
                    <td className="py-3.5 px-4">
                      <StatusBadge status={lead.status} />
                    </td>
                    <td className="py-3.5 px-4">
                      <PriorityBadge priority={lead.priority} />
                    </td>
                    <td className="py-3.5 px-4">
                      <ScoreBadge score={lead.aiScore} />
                    </td>
                    <td className="py-3.5 px-4 text-slate-600 font-medium">
                      {lead.assignedTo?.fullName || 'Unassigned'}
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      <div className="flex items-center justify-end space-x-1">
                        <Link
                          to={`/leads/${lead.id}`}
                          title="View Details"
                          className="p-1.5 text-slate-500 hover:text-indigo-600 hover:bg-slate-100 rounded-lg transition"
                        >
                          <Eye className="w-4 h-4" />
                        </Link>
                        <button
                          title="Edit Lead"
                          onClick={() => {
                            setSelectedLead(lead);
                            setModalOpen(true);
                          }}
                          className="p-1.5 text-slate-500 hover:text-purple-600 hover:bg-slate-100 rounded-lg transition"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          title="Delete Lead"
                          onClick={() => setDeleteConfirmId(lead.id)}
                          className="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-slate-100 rounded-lg transition"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Server-side Pagination Bar */}
        <div className="p-4 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-600">
          <div>
            Showing <span className="font-bold">{leads.length}</span> of <span className="font-bold">{totalElements}</span> leads
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setPage(Math.max(0, page - 1))}
              disabled={page === 0}
              className="p-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40 transition"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="font-semibold px-2">
              Page {page + 1} of {totalPages}
            </span>
            <button
              onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
              disabled={page >= totalPages - 1}
              className="p-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40 transition"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      {deleteConfirmId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-in fade-in">
          <div className="bg-white rounded-2xl max-w-sm w-full p-6 shadow-2xl border border-slate-100 text-center space-y-4">
            <div className="w-12 h-12 rounded-full bg-rose-50 text-rose-600 flex items-center justify-center mx-auto">
              <AlertCircle className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900">Delete this lead?</h3>
              <p className="text-xs text-slate-500 mt-1">This action cannot be undone. Associated activities and follow-ups will also be removed.</p>
            </div>
            <div className="flex items-center justify-center space-x-3 pt-2">
              <button
                onClick={() => setDeleteConfirmId(null)}
                className="px-4 py-2 border border-slate-300 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                onClick={() => handleDelete(deleteConfirmId)}
                className="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-semibold shadow"
              >
                Confirm Delete
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Lead Create/Edit Modal */}
      {modalOpen && (
        <LeadModal
          isOpen={modalOpen}
          initialLead={selectedLead}
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
