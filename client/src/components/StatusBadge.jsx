import React from 'react';

const statusConfig = {
  NEW: { label: 'New', bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-200' },
  CONTACTED: { label: 'Contacted', bg: 'bg-purple-50', text: 'text-purple-700', border: 'border-purple-200' },
  QUALIFIED: { label: 'Qualified', bg: 'bg-indigo-50', text: 'text-indigo-700', border: 'border-indigo-200' },
  PROPOSAL: { label: 'Proposal', bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
  NEGOTIATION: { label: 'Negotiation', bg: 'bg-orange-50', text: 'text-orange-700', border: 'border-orange-200' },
  CONVERTED: { label: 'Converted', bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
  LOST: { label: 'Lost', bg: 'bg-slate-100', text: 'text-slate-600', border: 'border-slate-200' },
};

export default function StatusBadge({ status }) {
  const config = statusConfig[status] || { label: status, bg: 'bg-slate-50', text: 'text-slate-700', border: 'border-slate-200' };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${config.bg} ${config.text} ${config.border}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current mr-1.5 opacity-70"></span>
      {config.label}
    </span>
  );
}
