import axios from 'axios'

// @ts-ignore
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15_000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('iqfleet_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('iqfleet_token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)
