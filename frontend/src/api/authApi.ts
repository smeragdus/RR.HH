import api from './axios'
import { JwtResponse, ApiResponse, User, ChangePasswordRequest } from '../types'

export const authApi = {
  login: async (username: string, password: string): Promise<ApiResponse<JwtResponse>> => {
    const response = await api.post<ApiResponse<JwtResponse>>('/auth/login', { username, password })
    return response.data
  },

  getCurrentUser: async (): Promise<ApiResponse<User>> => {
    const response = await api.get<ApiResponse<User>>('/auth/me')
    return response.data
  },

  changePassword: async (data: ChangePasswordRequest): Promise<ApiResponse<void>> => {
    const response = await api.put<ApiResponse<void>>('/auth/password', data)
    return response.data
  },
}
