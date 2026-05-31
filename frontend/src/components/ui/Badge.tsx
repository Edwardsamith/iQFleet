import { type HTMLAttributes } from 'react'
import { cn } from '@/lib/utils'
import type {
  DocumentStatus,
  DriverStatus,
  VehicleStatus,
  UserStatus,
  MovementStatus,
  MovementType,
} from '@/types'

export type BadgeVariant =
  | 'default'
  | 'blue'
  | 'success'
  | 'warning'
  | 'danger'
  | 'inactive'

// ─── Base Badge ───────────────────────────────────────────────────────────────

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant
  dot?: boolean
}

const variantStyles: Record<BadgeVariant, string> = {
  default:  'bg-blue-50 text-blue-600 border border-blue-100',
  blue:     'bg-blue-600 text-white',
  success:  'bg-ok-bg text-ok border border-ok-border',
  warning:  'bg-warn-bg text-warn border border-warn-border',
  danger:   'bg-err-bg text-err border border-err-border',
  inactive: 'bg-muted-bg text-muted border border-muted-border',
}

const dotStyles: Record<BadgeVariant, string> = {
  default:  'bg-blue-400',
  blue:     'bg-white',
  success:  'bg-ok',
  warning:  'bg-warn',
  danger:   'bg-err',
  inactive: 'bg-muted',
}

export function Badge({ variant = 'default', dot = false, className, children, ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-medium',
        variantStyles[variant],
        className,
      )}
      {...props}
    >
      {dot && (
        <span className={cn('inline-block w-1.5 h-1.5 rounded-full shrink-0', dotStyles[variant])} />
      )}
      {children}
    </span>
  )
}

// ─── Domain Badges ────────────────────────────────────────────────────────────

const documentStatusMap: Record<DocumentStatus, { variant: BadgeVariant; label: string }> = {
  VALID:         { variant: 'success',  label: 'Vigente' },
  EXPIRING_SOON: { variant: 'warning',  label: 'Por vencer' },
  EXPIRED:       { variant: 'danger',   label: 'Vencido' },
  INACTIVE:      { variant: 'inactive', label: 'Inactivo' },
  NO_EXPIRY:     { variant: 'default',  label: 'Sin vencimiento' },
}

export function DocumentStatusBadge({ status }: { status: DocumentStatus }) {
  const { variant, label } = documentStatusMap[status]
  return <Badge variant={variant} dot>{label}</Badge>
}

const driverStatusMap: Record<DriverStatus, { variant: BadgeVariant; label: string }> = {
  ACTIVE:   { variant: 'success',  label: 'Activo' },
  INACTIVE: { variant: 'inactive', label: 'Inactivo' },
}

export function DriverStatusBadge({ status }: { status: DriverStatus }) {
  const { variant, label } = driverStatusMap[status]
  return <Badge variant={variant} dot>{label}</Badge>
}

const vehicleStatusMap: Record<VehicleStatus, { variant: BadgeVariant; label: string }> = {
  ACTIVE:            { variant: 'success',  label: 'Activo' },
  UNDER_MAINTENANCE: { variant: 'warning',  label: 'Mantenimiento' },
  INACTIVE:          { variant: 'inactive', label: 'Inactivo' },
}

export function VehicleStatusBadge({ status }: { status: VehicleStatus }) {
  const { variant, label } = vehicleStatusMap[status]
  return <Badge variant={variant} dot>{label}</Badge>
}

const userStatusMap: Record<UserStatus, { variant: BadgeVariant; label: string }> = {
  PENDING:  { variant: 'warning',  label: 'Pendiente' },
  ACTIVE:   { variant: 'success',  label: 'Activo' },
  REJECTED: { variant: 'danger',   label: 'Rechazado' },
  INACTIVE: { variant: 'inactive', label: 'Inactivo' },
  BLOCKED:  { variant: 'danger',   label: 'Bloqueado' },
}

export function UserStatusBadge({ status }: { status: UserStatus }) {
  const { variant, label } = userStatusMap[status]
  return <Badge variant={variant} dot>{label}</Badge>
}

const movementStatusMap: Record<MovementStatus, { variant: BadgeVariant; label: string }> = {
  ACTIVE:    { variant: 'success',  label: 'Activo' },
  CANCELLED: { variant: 'inactive', label: 'Cancelado' },
}

export function MovementStatusBadge({ status }: { status: MovementStatus }) {
  const { variant, label } = movementStatusMap[status]
  return <Badge variant={variant}>{label}</Badge>
}

export function MovementTypeBadge({ type }: { type: MovementType }) {
  return (
    <Badge variant={type === 'INCOME' ? 'success' : 'danger'}>
      {type === 'INCOME' ? 'Ingreso' : 'Egreso'}
    </Badge>
  )
}
