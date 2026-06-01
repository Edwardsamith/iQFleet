import { http } from '@/lib/http'
import type { Driver, DriverStatus, LicenseCategory, IdentificationType } from '@/types/domain'

export interface CreateDriverPayload {
  identificationType: IdentificationType
  identificationNumber: string
  firstName: string
  lastName: string
  licenseNumber: string
  licenseCategory: LicenseCategory
  licenseExpiry: string
  phone?: string
  email?: string
  address?: string
  registrationDate?: string
}

export interface UpdateDriverPayload {
  firstName: string
  lastName: string
  licenseCategory: LicenseCategory
  licenseExpiry: string
  phone?: string
  email?: string
  address?: string
}

export async function listDrivers(filters?: {
  status?: DriverStatus
  licenseCategory?: LicenseCategory
  expiresInDays?: number
}): Promise<Driver[]> {
  const params = new URLSearchParams()
  if (filters?.status) params.set('status', filters.status)
  if (filters?.licenseCategory) params.set('licenseCategory', filters.licenseCategory)
  if (filters?.expiresInDays != null) params.set('expiresInDays', String(filters.expiresInDays))
  const query = params.toString()
  const response = await http.get<Driver[]>(`/drivers${query ? `?${query}` : ''}`)
  return response.data
}

export async function getDriverById(id: string): Promise<Driver> {
  const response = await http.get<Driver>(`/drivers/${id}`)
  return response.data
}

export async function createDriver(payload: CreateDriverPayload): Promise<void> {
  await http.post('/drivers', payload)
}

export async function updateDriver(id: string, payload: UpdateDriverPayload): Promise<void> {
  await http.put(`/drivers/${id}`, payload)
}

export async function changeDriverStatus(id: string, status: DriverStatus): Promise<void> {
  await http.patch(`/drivers/${id}/status`, { status })
}
