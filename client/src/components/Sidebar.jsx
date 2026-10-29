import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Users,
  KanbanSquare,
  CalendarCheck2,
  Sparkles,
  ShieldCheck,
  TrendingUp,
  LogOut,
  ChevronRight
} from 'lucide-react';

export default function Sidebar({ isOpen, setIsOpen }) {
  const { user, isAdmin, logout } = useAuth();

  const navItems = [
    { name: 'Dashboard', path: '/', icon: LayoutDashboard },
    { name: 'Leads', path: '/leads', icon: Users },
    { name: 'Sales Pipeline', path: '/pipeline', icon: KanbanSquare },
    { name: 'Follow-ups', path: '/follow-ups', icon: CalendarCheck2 },
    { name: 'AI Sales Assistant', path: '/ai-assistant', icon: Sparkles, badge: 'AI' },
  ];

  if (isAdmin) {
    navItems.push({ name: 'Team Users', path: '/users', icon: ShieldCheck });
  }

  return (
    <aside
      className={`fixed inset-y-0 left-0 z-40 w-64 bg-[#0B132B] text-slate-300 flex flex-col transition-transform duration-200 ease-in-out lg:translate-x-0 ${
        isOpen ? 'translate-x-0' : '-translate-x-full'
      }`}
    >
      {/* Brand Header */}
      <div className="h-16 flex items-center px-6 border-b border-slate-800 bg-[#070c1d]">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-teal-400 flex items-center justify-center shadow-lg shadow-indigo-500/25">
            <TrendingUp className="w-5 h-5 text-white" />
          </div>
          <div>
            <span className="text-base font-bold text-white tracking-tight">Smart CRM</span>
            <span className="block text-[10px] uppercase tracking-wider text-teal-400 font-semibold">AI Powered</span>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
        <div className="px-3 pb-2 text-[11px] font-semibold uppercase tracking-wider text-slate-500">
          Core Workspace
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === '/'}
              onClick={() => setIsOpen(false)}
              className={({ isActive }) =>
                `flex items-center justify-between px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-indigo-600/20 text-indigo-300 border border-indigo-500/30 font-semibold'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/60'
                }`
              }
            >
              <div className="flex items-center space-x-3">
                <Icon className="w-4 h-4" />
                <span>{item.name}</span>
              </div>
              {item.badge && (
                <span className="px-1.5 py-0.5 text-[10px] font-bold rounded bg-gradient-to-r from-purple-500 to-indigo-500 text-white shadow-sm">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* User profile & Logout */}
      <div className="p-3 border-t border-slate-800/80 bg-[#070c1d]/60">
        <div className="flex items-center justify-between px-2 py-2">
          <div className="flex items-center space-x-3 min-w-0">
            <div className="w-8 h-8 rounded-full bg-indigo-950 border border-indigo-700/50 flex items-center justify-center text-xs font-bold text-indigo-300 uppercase">
              {user?.fullName?.charAt(0) || 'U'}
            </div>
            <div className="truncate">
              <p className="text-xs font-semibold text-white truncate">{user?.fullName}</p>
              <span className={`inline-block text-[10px] font-bold uppercase tracking-wider px-1.5 py-0.2 rounded ${
                isAdmin ? 'bg-purple-900/40 text-purple-300' : 'bg-slate-800 text-slate-300'
              }`}>
                {user?.role}
              </span>
            </div>
          </div>
          <button
            onClick={logout}
            title="Log out"
            className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800/60 rounded-lg transition"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
}
