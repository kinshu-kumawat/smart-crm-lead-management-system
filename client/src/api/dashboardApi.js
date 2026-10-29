import api from './axiosClient';

export const dashboardApi = {
  getSummary: async () => {
    const res = await api.get('/dashboard/summary');
    return res.data;
  },

  getAnalytics: async () => {
    const res = await api.get('/dashboard/analytics');
    return res.data;
  },
};
