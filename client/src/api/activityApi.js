import api from './axiosClient';

export const activityApi = {
  getActivitiesByLead: async (leadId) => {
    const res = await api.get(`/leads/${leadId}/activities`);
    return res.data;
  },

  createActivity: async (leadId, activityData) => {
    const res = await api.post(`/leads/${leadId}/activities`, activityData);
    return res.data;
  },

  getRecentActivities: async () => {
    const res = await api.get('/activities/recent');
    return res.data;
  },
};
