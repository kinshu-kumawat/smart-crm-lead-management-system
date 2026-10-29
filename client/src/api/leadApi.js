import api from './axiosClient';

export const leadApi = {
  getLeads: async (params = {}) => {
    const res = await api.get('/leads', { params });
    return res.data;
  },

  getAllLeads: async () => {
    const res = await api.get('/leads/all');
    return res.data;
  },

  getLeadById: async (id) => {
    const res = await api.get(`/leads/${id}`);
    return res.data;
  },

  createLead: async (leadData) => {
    const res = await api.post('/leads', leadData);
    return res.data;
  },

  updateLead: async (id, leadData) => {
    const res = await api.put(`/leads/${id}`, leadData);
    return res.data;
  },

  updateLeadStatus: async (id, status, notes = '') => {
    const res = await api.patch(`/leads/${id}/status`, { status, notes });
    return res.data;
  },

  deleteLead: async (id) => {
    const res = await api.delete(`/leads/${id}`);
    return res.data;
  },

  getLeadHistory: async (id) => {
    const res = await api.get(`/leads/${id}/history`);
    return res.data;
  },

  exportCsvUrl: () => {
    const baseURL = import.meta.env.VITE_API_BASE_URL || '/api';
    return `${baseURL}/reports/export`;
  },
};
