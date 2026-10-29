import api from './axiosClient';

export const authApi = {
  login: async (credentials) => {
    const res = await api.post('/auth/login', credentials);
    return res.data;
  },

  register: async (userData) => {
    const res = await api.post('/auth/register', userData);
    return res.data;
  },

  logout: async () => {
    try {
      await api.post('/auth/logout');
    } catch (e) {
      // ignore server logout error, client will clean up storage
    }
  },

  getCurrentUser: async () => {
    const res = await api.get('/auth/me');
    return res.data;
  },
};
