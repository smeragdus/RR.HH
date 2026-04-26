import api from './axios'
import { ApiResponse, Request, Page, RequestType, RequestStatus } from '../types'

export const requestApi = {
  getAll: async (page = 0, size = 20, status?: RequestStatus): Promise<ApiResponse<Page<Request>>> => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() })
    if (status) params.append('status', status)
    const response = await api.get<ApiResponse<Page<Request>>>(`/requests?${params}`)
    return response.data
  },

  getById: async (id: number): Promise<ApiResponse<Request>> => {
    const response = await api.get<ApiResponse<Request>>(`/requests/${id}`)
    return response.data
  },

  getMyRequests: async (page = 0, size = 20): Promise<ApiResponse<Page<Request>>> => {
    const response = await api.get<ApiResponse<Page<Request>>>(
      `/requests/my?page=${page}&size=${size}`
    )
    return response.data
  },

  getPending: async (page = 0, size = 20): Promise<ApiResponse<Page<Request>>> => {
    const response = await api.get<ApiResponse<Page<Request>>>(
      `/requests/pending?page=${page}&size=${size}`
    )
    return response.data
  },

  create: async (data: { tipo: RequestType; fechaInicio: string; fechaFin: string; motivo?: string }): Promise<ApiResponse<Request>> => {
    const response = await api.post<ApiResponse<Request>>('/requests', data)
    return response.data
  },

  approve: async (id: number, comentarios?: string): Promise<ApiResponse<Request>> => {
    const response = await api.post<ApiResponse<Request>>(`/requests/${id}/approve`, { comentarios })
    return response.data
  },

  reject: async (id: number, comentarios?: string): Promise<ApiResponse<Request>> => {
    const response = await api.post<ApiResponse<Request>>(`/requests/${id}/reject`, { comentarios })
    return response.data
  },

  cancel: async (id: number): Promise<ApiResponse<Request>> => {
    const response = await api.post<ApiResponse<Request>>(`/requests/${id}/cancel`)
    return response.data
  },
}
