import React from 'react';

const priorityConfig = {
  HIGH: { label: 'High Priority', bg: 'bg-rose-50', text: 'text-rose-700', border: 'border-rose-200' },
  MEDIUM: { label: 'Medium Priority', bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
  LOW: { label: 'Low Priority', bg: 'bg-slate-50', text: 'text-slate-600', border: 'border-slate-200' },
};

export default function PriorityBadge({ priority }) {
  const config = priorityConfig[priority] || { label: priority, bg: 'bg-slate-50', text: 'text-slate-600', border: 'border-slate-200' };

  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${config.bg} ${config.text} ${config.border}`}>
      {config.label}
    </span>
  );
}
