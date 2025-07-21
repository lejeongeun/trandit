
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getRequests = () => {
  const token = localStorage.getItem('accessToken'); // 또는 sessionStorage, recoil 등에서 가져오기

  return apiClient.get('/request', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};
