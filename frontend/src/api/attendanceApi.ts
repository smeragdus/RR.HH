import api from './axios'
import { ApiResponse, Attendance, Page } from '../types'

export const attendanceApi = {
  checkIn: async (): Promise<ApiResponse<Attendance>> => {
    const response = await api.post<ApiResponse<Attendance>>('/attendance/checkin')
    return response.data
  },

  checkOut: async (): Promise<ApiResponse<Attendance>> => {
    const response = await api.post<ApiResponse<Attendance>>('/attendance/checkout')
    return response.data
  },

  getAll: async (page = 0, size = 20, fecha?: string): Promise<ApiResponse<Page<Attendance>>> => {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() })
    if (fecha) params.append('fecha', fecha)
    const response = await api.get<ApiResponse<Page<Attendance>>>(`/attendance?${params}`)
    return response.data
  },

  getToday: async (): Promise<ApiResponse<Attendance[]>> => {
    const response = await api.get<ApiResponse<Attendance[]>>('/attendance/today')
    return response.data
  },

  getMyToday: async (): Promise<ApiResponse<Attendance>> => {
    const response = await api.get<ApiResponse<Attendance>>('/attendance/me')
    return response.data
  },
}
