import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import LeadModal from './LeadModal';

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [newLeadModalOpen, setNewLeadModalOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Mobile Backdrop */}
      {sidebarOpen && (
        <div
          onClick={() => setSidebarOpen(false)}
          className="fixed inset-0 bg-slate-900/50 z-30 lg:hidden backdrop-blur-sm transition-opacity"
        />
      )}

      {/* Sidebar */}
      <Sidebar isOpen={sidebarOpen} setIsOpen={setSidebarOpen} />

      {/* Main Container */}
      <div className="flex-1 flex flex-col min-w-0 lg:pl-64">
        <Navbar
          onOpenSidebar={() => setSidebarOpen(true)}
          onOpenNewLead={() => setNewLeadModalOpen(true)}
        />
        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          <Outlet context={{ openNewLeadModal: () => setNewLeadModalOpen(true) }} />
        </main>
      </div>

      {/* Global Quick Add Lead Modal */}
      {newLeadModalOpen && (
        <LeadModal
          isOpen={newLeadModalOpen}
          onClose={() => setNewLeadModalOpen(false)}
          onSuccess={() => {
            setNewLeadModalOpen(false);
            window.dispatchEvent(new CustomEvent('lead-created'));
          }}
        />
      )}
    </div>
  );
}
