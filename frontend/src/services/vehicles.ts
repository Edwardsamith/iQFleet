import { http } from '@/lib/http'
import type { Vehicle, VehicleStatus, VehicleType } from '@/types/domain'

export interface CreateVehiclePayload {
  plateNumber: string
  brand: string
  vehicleModel: string
  vehicleType: VehicleType
  year?: number
  status?: VehicleStatus
  responsibleName?: string
  registrationDate?: string
  notes?: string
}

export interface UpdateVehiclePayload {
  brand: string
  vehicleModel: string
  vehicleType: VehicleType
  year?: number
  responsibleName?: string
  notes?: string
}

export async function listVehicles(filters?: {
  status?: VehicleStatus
  withoutDriver?: boolean
  page?: number
  size?: number
}): Promise<Vehicle[]> {
  const params = new URLSearchParams()
  if (filters?.status) params.set('status', filters.status)
  if (filters?.withoutDriver) params.set('sindriver', 'true')
  if (filters?.page != null) params.set('page', String(filters.page))
  if (filters?.size != null) params.set('size', String(filters.size))
  const query = params.toString()
  const response = await http.get<Vehicle[]>(`/vehicles${query ? `?${query}` : ''}`)
  return response.data
}

export async function getVehicleById(id: string): Promise<Vehicle> {
  const response = await http.get<Vehicle>(`/vehicles/${id}`)
  return response.data
}

export async function createVehicle(payload: CreateVehiclePayload): Promise<void> {
  await http.post('/vehicles', payload)
}

export async function updateVehicle(id: string, payload: UpdateVehiclePayload): Promise<void> {
  await http.put(`/vehicles/${id}`, payload)
}

export async function changeVehicleStatus(id: string, status: VehicleStatus): Promise<void> {
  await http.patch(`/vehicles/${id}/status`, { status })
}

export async function assignDriver(vehicleId: string, driverId: string): Promise<void> {
  await http.patch(`/vehicles/${vehicleId}/driver`, { driverId })
}

export async function unassignDriver(vehicleId: string): Promise<void> {
  await http.patch(`/vehicles/${vehicleId}/driver`, { driverId: null })
}
