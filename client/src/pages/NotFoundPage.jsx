import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, AlertCircle } from 'lucide-react';

export default function NotFoundPage() {
  return (
    <div className="min-h-[70vh] flex flex-col items-center justify-center text-center p-6 space-y-4">
      <div className="w-16 h-16 rounded-3xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
        <AlertCircle className="w-8 h-8" />
      </div>
      <div>
        <h1 className="text-3xl font-black text-slate-800">404</h1>
        <p className="text-sm font-semibold text-slate-600 mt-1">Page not found</p>
        <p className="text-xs text-slate-400 mt-1">The CRM resource you requested does not exist or has been moved.</p>
      </div>
      <Link
        to="/"
        className="inline-flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-semibold shadow-sm transition"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Return to Dashboard</span>
      </Link>
    </div>
  );
}
