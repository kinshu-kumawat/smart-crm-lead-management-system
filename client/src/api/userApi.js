import api from './axiosClient';

export const userApi = {
  getUsers: async () => {
    const res = await api.get('/users');
    return res.data;
  },

  getUserById: async (id) => {
    const res = await api.get(`/users/${id}`);
    return res.data;
  },

  createUser: async (userData) => {
    const res = await api.post('/users', userData);
    return res.data;
  },

  toggleUserStatus: async (id) => {
    const res = await api.patch(`/users/${id}/toggle-status`);
    return res.data;
  },
};
