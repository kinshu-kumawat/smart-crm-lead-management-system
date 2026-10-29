import React from 'react';

export default function ScoreBadge({ score }) {
  if (score === null || score === undefined) {
    return (
      <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-500">
        Unscored
      </span>
    );
  }

  let colorClasses = 'bg-slate-100 text-slate-700 border-slate-200';
  if (score >= 80) {
    colorClasses = 'bg-emerald-50 text-emerald-700 border-emerald-300 font-bold';
  } else if (score >= 60) {
    colorClasses = 'bg-indigo-50 text-indigo-700 border-indigo-300 font-semibold';
  } else if (score >= 40) {
    colorClasses = 'bg-amber-50 text-amber-700 border-amber-300';
  } else {
    colorClasses = 'bg-rose-50 text-rose-700 border-rose-300';
  }

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs border ${colorClasses}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current mr-1.5"></span>
      {score}/100
    </span>
  );
}
