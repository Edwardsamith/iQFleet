// ─── Enums ────────────────────────────────────────────────────────────────────

export type DocumentType =
  | 'SOAT'
  | 'RTM'
  | 'OPERATION_CARD'
  | 'PROPERTY_CARD'
  | 'LIABILITY_POLICY'
  | 'DRIVING_LICENSE'
  | 'MEDICAL_CERTIFICATE'
  | 'EMPLOYMENT_CONTRACT'
  | 'TRAFFIC_CLEARANCE'
  | 'COMPANY_LICENSE'
  | 'ROUTE_CONTRACT'
  | 'OTHER_DRIVER'
  | 'OTHER_VEHICLE'
  | 'OTHER_ADMIN'

export type DocumentStatus = 'VALID' | 'EXPIRING_SOON' | 'EXPIRED' | 'INACTIVE' | 'NO_EXPIRY'

export type DriverStatus = 'ACTIVE' | 'INACTIVE'

export type VehicleStatus = 'ACTIVE' | 'UNDER_MAINTENANCE' | 'INACTIVE'

export type VehicleType = 'BUS' | 'TAXI' | 'MINIBUS' | 'MICROBUS' | 'VAN'

export type LicenseCategory = 'A1' | 'A2' | 'B1' | 'B2' | 'B3' | 'C1' | 'C2' | 'C3'

export type IdentificationType = 'CC' | 'CE' | 'PA'

export type Role = 'ROLE_OWNER' | 'ROLE_ADMIN'

export type UserStatus = 'PENDING' | 'ACTIVE' | 'REJECTED' | 'INACTIVE' | 'BLOCKED'

export type MovementType = 'INCOME' | 'EXPENSE'

export type MovementCategory =
  | 'DAILY_COLLECTION'
  | 'SUBSIDY'
  | 'OTHER_INCOME'
  | 'FUEL'
  | 'PREVENTIVE_MAINTENANCE'
  | 'CORRECTIVE_MAINTENANCE'
  | 'SALARY'
  | 'INSURANCE'
  | 'PAPERWORK'
  | 'TAX'
  | 'OTHER_EXPENSE'

export type MovementStatus = 'ACTIVE' | 'CANCELLED'

export type PaymentMethod = 'CASH' | 'BANK_TRANSFER'

// ─── Entities ─────────────────────────────────────────────────────────────────

export interface BaseEntity {
  id: string
  createdAt: string
  updatedAt: string
}

export interface Document extends BaseEntity {
  name: string
  documentType: DocumentType
  referenceNumber?: string
  issuingEntity?: string
  issueDate?: string
  expiryDate?: string
  status: DocumentStatus
  fileUrl?: string
  fileFormat?: string
  notes?: string
  driverId?: string
  vehicleId?: string
  uploadedBy: string
  uploadDate: string
}

export interface Driver extends BaseEntity {
  identificationType: IdentificationType
  identificationNumber: string
  firstName: string
  lastName: string
  licenseNumber: string
  licenseCategory: LicenseCategory
  licenseExpiry: string
  phone: string
  email?: string
  address?: string
  status: DriverStatus
  registrationDate: string
  deactivationDate?: string
  deactivationReason?: string
}

export interface Vehicle extends BaseEntity {
  plateNumber: string
  brand: string
  vehicleModel: string
  vehicleType: VehicleType
  year: number
  status: VehicleStatus
  responsibleId?: string
  registrationDate: string
  notes?: string
  assignedDriverId?: string
}

export interface User extends BaseEntity {
  firstName: string
  lastName: string
  identificationType: IdentificationType
  identificationNumber: string
  email: string
  phone?: string
  username: string
  role: Role
  status: UserStatus
  lastAccess?: string
}

export interface FinancialMovement extends BaseEntity {
  movementType: MovementType
  category: MovementCategory
  paymentMethod: PaymentMethod
  amount: number
  date: string
  description: string
  notes?: string
  status: MovementStatus
  vehicleId?: string
  driverId?: string
  registeredBy: string
}

// ─── API Response Wrappers ────────────────────────────────────────────────────

export interface ApiResult<T = void> {
  isSuccess: boolean
  value?: T
  error?: string
}

export interface PagedResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  page: number
  size: number
}
