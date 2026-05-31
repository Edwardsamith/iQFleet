import { type ReactNode, type TdHTMLAttributes, type ThHTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

interface TableProps {
  children: ReactNode
  className?: string
}

export function Table({ children, className }: TableProps) {
  return (
    <div className={cn('w-full overflow-x-auto', className)}>
      <table className="w-full text-sm border-collapse">{children}</table>
    </div>
  )
}

Table.Head = function TableHead({ children }: { children: ReactNode }) {
  return (
    <thead className="bg-blue-50/60 border-b border-blue-100">
      {children}
    </thead>
  )
}

Table.Body = function TableBody({ children }: { children: ReactNode }) {
  return (
    <tbody className="divide-y divide-blue-50 bg-white">
      {children}
    </tbody>
  )
}

interface RowProps {
  children: ReactNode
  onClick?: () => void
  className?: string
}

Table.Row = function TableRow({ children, onClick, className }: RowProps) {
  return (
    <tr
      onClick={onClick}
      className={cn(
        'transition-colors duration-fast',
        onClick && 'cursor-pointer hover:bg-blue-50/40',
        className,
      )}
    >
      {children}
    </tr>
  )
}

interface ThProps extends ThHTMLAttributes<HTMLTableCellElement> {
  children?: ReactNode
}

Table.Th = function TableTh({ children, className, ...props }: ThProps) {
  return (
    <th
      className={cn(
        'px-4 py-3 text-left text-xs font-semibold text-blue-400 uppercase tracking-wider',
        className,
      )}
      {...props}
    >
      {children}
    </th>
  )
}

interface TdProps extends TdHTMLAttributes<HTMLTableCellElement> {
  children?: ReactNode
}

Table.Td = function TableTd({ children, className, ...props }: TdProps) {
  return (
    <td className={cn('px-4 py-3.5 text-slate-600', className)} {...props}>
      {children}
    </td>
  )
}

interface EmptyStateProps {
  icon?: ReactNode
  title: string
  description?: string
  action?: ReactNode
  colSpan?: number
}

Table.Empty = function TableEmpty({ icon, title, description, action, colSpan = 99 }: EmptyStateProps) {
  return (
    <tr>
      <td colSpan={colSpan}>
        <div className="flex flex-col items-center justify-center py-16 text-center px-4">
          {icon && <div className="text-blue-200 mb-3">{icon}</div>}
          <p className="text-sm font-medium text-slate-500">{title}</p>
          {description && <p className="text-xs text-slate-400 mt-1 max-w-xs">{description}</p>}
          {action && <div className="mt-4">{action}</div>}
        </div>
      </td>
    </tr>
  )
}

Table.Skeleton = function TableSkeleton({ cols, rows = 5 }: { cols: number; rows?: number }) {
  return (
    <>
      {Array.from({ length: rows }).map((_, i) => (
        <tr key={i} className="animate-pulse">
          {Array.from({ length: cols }).map((_, j) => (
            <td key={j} className="px-4 py-3.5">
              <div className="h-3.5 bg-blue-50 rounded-full w-3/4" />
            </td>
          ))}
        </tr>
      ))}
    </>
  )
}
