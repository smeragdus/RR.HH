import api from './axios'
import { ApiResponse, Contract, Page } from '../types'

export const contractApi = {
  getAll: async (page = 0, size = 20): Promise<ApiResponse<Page<Contract>>> => {
    const response = await api.get<ApiResponse<Page<Contract>>>(
      `/contracts?page=${page}&size=${size}`
    )
    return response.data
  },

  getById: async (id: number): Promise<ApiResponse<Contract>> => {
    const response = await api.get<ApiResponse<Contract>>(`/contracts/${id}`)
    return response.data
  },

  create: async (data: Partial<Contract>): Promise<ApiResponse<Contract>> => {
    const response = await api.post<ApiResponse<Contract>>('/contracts', data)
    return response.data
  },

  update: async (id: number, data: Partial<Contract>): Promise<ApiResponse<Contract>> => {
    const response = await api.put<ApiResponse<Contract>>(`/contracts/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/contracts/${id}`)
    return response.data
  },
}
