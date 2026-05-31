import { http } from '@/lib/http'
import type { User } from '@/types/domain'

export interface UserDetail extends User {
  identificationNumber: string
  identificationType: string
}

export interface PendingRequest {
  solicitudId: string
  usuarioId: string
  firstName: string
  lastName: string
  email: string
  username: string
  identificationType: string
  identificationNumber: string
  phone?: string
  rolSolicitado: string
  fechaCreacion: string
}

export interface CreateUserPayload {
  firstName: string
  lastName: string
  identificationType: string
  identificationNumber: string
  email: string
  phone?: string
  username: string
  password: string
  role: string
  status?: string
}

export interface UpdateUserPayload {
  firstName?: string
  lastName?: string
  phone?: string
  email?: string
  role?: string
}

export async function listUsers(filters?: {
  status?: string
  role?: string
}): Promise<UserDetail[]> {
  const params = new URLSearchParams()
  if (filters?.status) params.set('status', filters.status)
  if (filters?.role) params.set('role', filters.role)
  const query = params.toString()
  const response = await http.get<UserDetail[]>(`/users${query ? `?${query}` : ''}`)
  return response.data
}

export async function getUserById(id: string): Promise<UserDetail> {
  const response = await http.get<UserDetail>(`/users/${id}`)
  return response.data
}

export async function createUser(payload: CreateUserPayload): Promise<{
  userId: string
  username: string
  message: string
}> {
  const response = await http.post('/users', payload)
  return response.data
}

export async function updateUser(
  id: string,
  payload: UpdateUserPayload,
): Promise<{ userId: string; username: string; message: string }> {
  const response = await http.put(`/users/${id}`, payload)
  return response.data
}

export async function changeUserStatus(id: string, status: string): Promise<string> {
  const response = await http.patch(`/users/${id}/status`, { status })
  return response.data
}

export async function adminResetPassword(id: string, newPassword: string): Promise<string> {
  const response = await http.patch(`/users/${id}/reset-password`, { newPassword })
  return response.data
}

export async function listPendingRequests(): Promise<PendingRequest[]> {
  const response = await http.get<PendingRequest[]>('/users/requests')
  return response.data
}

export async function approveRequest(solicitudId: string, role?: string): Promise<unknown> {
  const response = await http.patch(`/users/requests/${solicitudId}/approve`, role ? { role } : {})
  return response.data
}

export async function rejectRequest(solicitudId: string, reason?: string): Promise<unknown> {
  const response = await http.patch(`/users/requests/${solicitudId}/reject`, { reason })
  return response.data
}
