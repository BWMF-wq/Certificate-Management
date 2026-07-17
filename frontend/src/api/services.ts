import { api } from './client'
import type { AwardType, AuthResponse, Certificate, CertificatePayload, Category, DashboardData, Level, PageResponse, User } from '@/types'

export const authApi = {
  login: (data: { account: string; password: string }) => api.post<AuthResponse>('/auth/login', data),
  register: (data: { name: string; studentId: string; email: string; password: string }) => api.post<AuthResponse>('/auth/register', data),
}

export const userApi = {
  me: () => api.get<User>('/users/me'),
  update: (data: Partial<User>) => api.put<User>('/users/me', data),
}

export const certificateApi = {
  list: (params: { keyword?: string; category?: Category | ''; level?: Level | ''; awardType?: AwardType | ''; page?: number; size?: number; sort?: string }) =>
    api.get<PageResponse<Certificate>>('/certificates', { params }),
  get: (id: number) => api.get<Certificate>(`/certificates/${id}`),
  create: (data: CertificatePayload) => api.post<Certificate>('/certificates', data),
  update: (id: number, data: CertificatePayload) => api.put<Certificate>(`/certificates/${id}`, data),
  remove: (id: number) => api.delete(`/certificates/${id}`),
  trash: (params: { keyword?: string; page?: number; size?: number }) =>
    api.get<PageResponse<Certificate>>('/certificates/trash', { params }),
  restore: (id: number) => api.post<Certificate>(`/certificates/${id}/restore`),
  removePermanently: (id: number) => api.delete(`/certificates/${id}/permanent`),
  upload: (id: number, file: File) => {
    const data = new FormData(); data.append('file', file)
    return api.post<Certificate>(`/certificates/${id}/attachment`, data)
  },
  removeAttachment: (id: number) => api.delete<Certificate>(`/certificates/${id}/attachment`),
  download: (id: number) => api.get<Blob>(`/certificates/${id}/attachment`, { responseType: 'blob' }),
}

export const dashboardApi = { get: () => api.get<DashboardData>('/dashboard') }

