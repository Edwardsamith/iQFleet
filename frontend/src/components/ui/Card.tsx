import { type HTMLAttributes, type ReactNode } from 'react'
import { cn } from '@/lib/utils'

// ─── Card root ────────────────────────────────────────────────────────────────

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  noPadding?: boolean
}

export function Card({ className, children, noPadding = false, ...props }: CardProps) {
  // @ts-ignore
  return (
    <div
      className={cn(
        'bg-white rounded-2xl border border-blue-100 shadow-card',
        !noPadding && 'p-5',
        className,
      )}
      {...props}
    >
      {children}
    </div>
  )
}

// ─── Card.Header ──────────────────────────────────────────────────────────────

interface CardHeaderProps {
  title: string
  subtitle?: string
  action?: ReactNode
  className?: string
}

Card.Header = function CardHeader({ title, subtitle, action, className }: CardHeaderProps) {
  return (
    <div className={cn('flex items-start justify-between gap-4 mb-5', className)}>
      <div>
        <h3 className="text-sm font-semibold text-slate-800">{title}</h3>
        {subtitle && <p className="text-xs text-slate-400 mt-0.5">{subtitle}</p>}
      </div>
      {action && <div className="shrink-0">{action}</div>}
    </div>
  )
}

// ─── StatCard ─────────────────────────────────────────────────────────────────

interface StatCardProps {
  label: string
  value: string | number
  icon?: ReactNode
  trend?: { value: number; label: string }
  variant?: 'default' | 'success' | 'warning' | 'danger'
  className?: string
}

const statVariant = {
  default: { icon: 'bg-blue-50 text-blue-500',    value: 'text-slate-800' },
  success: { icon: 'bg-ok-bg text-ok',             value: 'text-ok' },
  warning: { icon: 'bg-warn-bg text-warn',         value: 'text-warn' },
  danger:  { icon: 'bg-err-bg text-err',           value: 'text-err' },
}

export function StatCard({ label, value, icon, trend, variant = 'default', className }: StatCardProps) {
  const styles = statVariant[variant]

  return (
    <Card className={cn('flex items-center gap-4', className)}>
      {icon && (
        <div className={cn('p-3 rounded-xl shrink-0', styles.icon)}>
          {icon}
        </div>
      )}
      <div className="min-w-0">
        <p className="text-xs text-slate-400 truncate font-medium">{label}</p>
        <p className={cn('text-2xl font-bold mt-0.5 tracking-tight', styles.value)}>{value}</p>
        {trend && (
          <p className={cn('text-xs mt-1', trend.value >= 0 ? 'text-ok' : 'text-err')}>
            {trend.value >= 0 ? '↑' : '↓'} {Math.abs(trend.value)}% {trend.label}
          </p>
        )}
      </div>
    </Card>
  )
}
