import React, { useState, useEffect } from 'react';
import { Menu, Sparkles, Plus, ExternalLink } from 'lucide-react';
import { aiApi } from '../api/aiApi';
import { useAuth } from '../context/AuthContext';

export default function Navbar({ onOpenSidebar, onOpenNewLead }) {
  const { user } = useAuth();
  const [aiStatus, setAiStatus] = useState({ configured: false, provider: 'Checking...' });

  useEffect(() => {
    aiApi.getStatus()
      .then(res => {
        if (res.success && res.data) {
          setAiStatus({
            configured: res.data.geminiConfigured,
            provider: res.data.provider,
            message: res.data.message
          });
        }
      })
      .catch(() => {
        setAiStatus({ configured: false, provider: 'Smart CRM Engine' });
      });
  }, []);

  return (
    <header className="h-16 bg-white border-b border-slate-200 sticky top-0 z-30 flex items-center justify-between px-4 sm:px-6">
      <div className="flex items-center space-x-3">
        <button
          onClick={onOpenSidebar}
          className="lg:hidden p-2 rounded-lg text-slate-600 hover:bg-slate-100 transition"
        >
          <Menu className="w-5 h-5" />
        </button>
        <div className="hidden sm:block">
          <span className="text-xs text-slate-400 font-medium">Welcome back,</span>
          <h2 className="text-sm font-bold text-slate-800 leading-tight">{user?.fullName}</h2>
        </div>
      </div>

      <div className="flex items-center space-x-3">
        {/* AI Status Badge */}
        <div
          title={aiStatus.message || aiStatus.provider}
          className={`flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-medium border cursor-help transition ${
            aiStatus.configured
              ? 'bg-purple-50 text-purple-700 border-purple-200 hover:bg-purple-100'
              : 'bg-indigo-50 text-indigo-700 border-indigo-200 hover:bg-indigo-100'
          }`}
        >
          <Sparkles className="w-3.5 h-3.5 text-purple-600 animate-pulse" />
          <span className="hidden sm:inline font-semibold">AI:</span>
          <span className="truncate max-w-[130px] sm:max-w-none">{aiStatus.provider}</span>
        </div>

        {/* Quick Lead Button */}
        {onOpenNewLead && (
          <button
            onClick={onOpenNewLead}
            className="flex items-center space-x-1.5 px-3.5 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold shadow-sm transition active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span className="hidden sm:inline">New Lead</span>
          </button>
        )}
      </div>
    </header>
  );
}
