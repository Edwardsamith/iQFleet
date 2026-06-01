import { http } from '@/lib/http'
import type {
  FinancialMovement,
  MovementType,
  MovementCategory,
  PaymentMethod,
} from '@/types/domain'

export interface RegisterMovementPayload {
  movementType: MovementType
  category: MovementCategory
  paymentMethod: PaymentMethod
  amount: number
  date: string
  description?: string
  notes?: string
  vehicleId: string
  driverId?: string
  registeredBy: string
}

export interface MovementsFilter {
  movementType?: MovementType
  category?: MovementCategory
  paymentMethod?: PaymentMethod
  vehicleId?: string
  dateFrom?: string
  dateTo?: string
}

export interface VehicleBalance {
  vehicleId: string
  plateNumber: string
  dateFrom: string
  dateTo: string
  totalIncome: number
  totalExpense: number
  netProfit: number
}

export interface FleetBalance {
  dateFrom: string
  dateTo: string
  totalVehicles: number
  totalIncome: number
  totalExpense: number
  netProfit: number
}

export async function listMovements(filters?: MovementsFilter): Promise<FinancialMovement[]> {
  const params = new URLSearchParams()
  if (filters?.movementType) params.set('movementType', filters.movementType)
  if (filters?.category) params.set('category', filters.category)
  if (filters?.paymentMethod) params.set('paymentMethod', filters.paymentMethod)
  if (filters?.vehicleId) params.set('vehicleId', filters.vehicleId)
  if (filters?.dateFrom) params.set('dateFrom', filters.dateFrom)
  if (filters?.dateTo) params.set('dateTo', filters.dateTo)
  const query = params.toString()
  const response = await http.get<FinancialMovement[]>(`/finances/movements${query ? `?${query}` : ''}`)
  return response.data
}

export async function getMovementById(id: string): Promise<FinancialMovement> {
  const response = await http.get<FinancialMovement>(`/finances/movements/${id}`)
  return response.data
}

export async function registerMovement(payload: RegisterMovementPayload): Promise<void> {
  await http.post('/finances/movements', payload)
}

export async function cancelMovement(id: string): Promise<void> {
  await http.patch(`/finances/movements/${id}/cancel`)
}

export async function getVehicleBalance(
  vehicleId: string,
  dateFrom: string,
  dateTo: string,
): Promise<VehicleBalance> {
  const response = await http.get<VehicleBalance>(
    `/finances/balance/vehicle/${vehicleId}?dateFrom=${dateFrom}&dateTo=${dateTo}`,
  )
  return response.data
}

export async function getFleetBalance(dateFrom: string, dateTo: string): Promise<FleetBalance> {
  const response = await http.get<FleetBalance>(
    `/finances/balance/fleet?dateFrom=${dateFrom}&dateTo=${dateTo}`,
  )
  return response.data
}
