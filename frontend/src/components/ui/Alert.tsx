import { type ReactNode } from 'react'
import { AlertTriangle, CheckCircle, Info, XCircle, X } from 'lucide-react'
import { cn } from '@/lib/utils'

export type AlertVariant = 'info' | 'success' | 'warning' | 'error'

interface AlertProps {
  variant?: AlertVariant
  title?: string
  children: ReactNode
  onClose?: () => void
  className?: string
}

const alertConfig: Record<
  AlertVariant,
  { icon: ReactNode; container: string; iconColor: string; titleColor: string }
> = {
  info: {
    icon: <Info size={15} />,
    container: 'bg-blue-50 border border-blue-100',
    iconColor: 'text-blue-500',
    titleColor: 'text-blue-700',
  },
  success: {
    icon: <CheckCircle size={15} />,
    container: 'bg-ok-bg border border-ok-border',
    iconColor: 'text-ok',
    titleColor: 'text-ok',
  },
  warning: {
    icon: <AlertTriangle size={15} />,
    container: 'bg-warn-bg border border-warn-border',
    iconColor: 'text-warn',
    titleColor: 'text-warn',
  },
  error: {
    icon: <XCircle size={15} />,
    container: 'bg-err-bg border border-err-border',
    iconColor: 'text-err',
    titleColor: 'text-err',
  },
}

export function Alert({ variant = 'info', title, children, onClose, className }: AlertProps) {
  const cfg = alertConfig[variant]

  return (
    <div
      role="alert"
      className={cn('flex gap-3 p-4 rounded-xl text-sm', cfg.container, className)}
    >
      <span className={cn('shrink-0 mt-0.5', cfg.iconColor)}>{cfg.icon}</span>

      <div className="flex-1 min-w-0">
        {title && <p className={cn('font-semibold text-xs uppercase tracking-wide mb-1', cfg.titleColor)}>{title}</p>}
        <div className="text-slate-600 text-sm">{children}</div>
      </div>

      {onClose && (
        <button
          onClick={onClose}
          className="shrink-0 text-slate-300 hover:text-slate-500 transition-colors duration-fast"
          aria-label="Cerrar"
        >
          <X size={14} />
        </button>
      )}
    </div>
  )
}

interface ToastProps {
  message: string
  variant?: AlertVariant
  onClose?: () => void
}

export function Toast({ message, variant = 'info', onClose }: ToastProps) {
  const cfg = alertConfig[variant]

  return (
    <div className={cn('flex items-center gap-3 px-4 py-3 rounded-xl border shadow-modal text-sm font-medium', cfg.container)}>
      <span className={cfg.iconColor}>{cfg.icon}</span>
      <span className="text-slate-700">{message}</span>
      {onClose && (
        <button onClick={onClose} className="ml-auto shrink-0 text-slate-300 hover:text-slate-500">
          <X size={14} />
        </button>
      )}
    </div>
  )
}
