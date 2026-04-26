import api from './axios'
import { ApiResponse, Employee, Page } from '../types'

export const employeeApi = {
  getAll: async (page = 0, size = 20, search?: string): Promise<ApiResponse<Page<Employee>>> => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() })
    if (search) params.append('search', search)
    const response = await api.get<ApiResponse<Page<Employee>>>(`/employees?${params}`)
    return response.data
  },

  getById: async (id: number): Promise<ApiResponse<Employee>> => {
    const response = await api.get<ApiResponse<Employee>>(`/employees/${id}`)
    return response.data
  },

  create: async (data: Partial<Employee>): Promise<ApiResponse<Employee>> => {
    const response = await api.post<ApiResponse<Employee>>('/employees', data)
    return response.data
  },

  update: async (id: number, data: Partial<Employee>): Promise<ApiResponse<Employee>> => {
    const response = await api.put<ApiResponse<Employee>>(`/employees/${id}`, data)
    return response.data
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/employees/${id}`)
    return response.data
  },
}
