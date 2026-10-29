import api from './axiosClient';

export const followUpApi = {
  getFollowUps: async (filter = 'ALL', leadId = null) => {
    const params = { filter };
    if (leadId) params.leadId = leadId;
    const res = await api.get('/follow-ups', { params });
    return res.data;
  },

  createFollowUp: async (data) => {
    const res = await api.post('/follow-ups', data);
    return res.data;
  },

  updateFollowUp: async (id, data) => {
    const res = await api.put(`/follow-ups/${id}`, data);
    return res.data;
  },

  completeFollowUp: async (id) => {
    const res = await api.patch(`/follow-ups/${id}/complete`);
    return res.data;
  },
};
