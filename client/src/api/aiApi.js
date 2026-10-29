import api from './axiosClient';

export const aiApi = {
  scoreLead: async (leadId) => {
    const res = await api.post('/ai/lead-score', { leadId });
    return res.data;
  },

  generateFollowUp: async (data) => {
    const res = await api.post('/ai/follow-up-message', data);
    return res.data;
  },

  generateSummary: async (leadId) => {
    const res = await api.post('/ai/lead-summary', { leadId });
    return res.data;
  },

  chat: async (data) => {
    const res = await api.post('/ai/chat', data);
    return res.data;
  },

  getStatus: async () => {
    const res = await api.get('/ai/status');
    return res.data;
  },
};
