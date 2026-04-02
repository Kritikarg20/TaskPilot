const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error.response?.data || error.message)
  }
)

export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
}

export const taskService = {
  getAll: () => api.get('/tasks'),
  getById: (id) => api.get(`/tasks/${id}`),
  create: (data) => api.post('/tasks', data),
  update: (id, data) => api.put(`/tasks/${id}`, data),
  delete: (id) => api.delete(`/tasks/${id}`),
  moveKanban: (id, data) => api.patch(`/tasks/${id}/kanban`, data),
}

export const subtaskService = {
  getByTask: (taskId) => api.get(`/subtasks/task/${taskId}`),
  create: (data) => api.post('/subtasks', data),
  update: (id, data) => api.put(`/subtasks/${id}`, data),
  delete: (id) => api.delete(`/subtasks/${id}`),
}

export const categoryService = {
  getAll: () => api.get('/categories'),
  create: (data) => api.post('/categories', data),
  update: (id, data) => api.put(`/categories/${id}`, data),
  delete: (id) => api.delete(`/categories/${id}`),
}

export const analyticsService = {
  get: () => api.get('/analytics'),
}

export const fileService = {
  getByTask: (taskId) => api.get(`/files/task/${taskId}`),
  upload: (taskId, formData) => api.post(`/files/upload/${taskId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  delete: (id) => api.delete(`/files/${id}`),
  downloadUrl: (id) => `/api/files/download/${id}`,
}

export const userService = {
  getProfile: () => api.get('/users/profile'),
  updateProfile: (data) => api.put('/users/profile', data),
}

export default api
