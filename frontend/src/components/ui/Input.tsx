import { forwardRef, type InputHTMLAttributes, type ReactNode } from 'react'
import { cn } from '@/lib/utils'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  hint?: string
  error?: string
  leadingIcon?: ReactNode
  trailingIcon?: ReactNode
}

const baseInput = [
  'w-full rounded-lg border bg-white text-sm text-slate-800',
  'placeholder:text-slate-300',
  'transition-all duration-normal',
  'focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-400',
  'disabled:bg-blue-50/50 disabled:text-slate-400 disabled:cursor-not-allowed',
].join(' ')

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, hint, error, leadingIcon, trailingIcon, className, id, ...props }, ref) => {
    const inputId = id ?? label?.toLowerCase().replace(/\s+/g, '-')

    return (
      <div className="flex flex-col gap-1.5">
        {label && (
          <label htmlFor={inputId} className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            {label}
            {props.required && <span className="text-blue-400 ml-0.5">*</span>}
          </label>
        )}

        <div className="relative">
          {leadingIcon && (
            <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-300 pointer-events-none">
              {leadingIcon}
            </span>
          )}

          <input
            ref={ref}
            id={inputId}
            className={cn(
              baseInput,
              error
                ? 'border-err/50 focus:ring-err/30 focus:border-err'
                : 'border-blue-100 hover:border-blue-200',
              leadingIcon ? 'pl-9' : 'pl-3.5',
              trailingIcon ? 'pr-9' : 'pr-3.5',
              'py-2 h-9',
              className,
            )}
            {...props}
          />

          {trailingIcon && (
            <span className="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-300 pointer-events-none">
              {trailingIcon}
            </span>
          )}
        </div>

        {error ? (
          <p className="text-xs text-err flex items-center gap-1">⚠ {error}</p>
        ) : hint ? (
          <p className="text-xs text-slate-400">{hint}</p>
        ) : null}
      </div>
    )
  },
)

Input.displayName = 'Input'

// ─── Select ───────────────────────────────────────────────────────────────────

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  hint?: string
  error?: string
  options: { value: string; label: string }[]
  placeholder?: string
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, hint, error, options, placeholder, className, id, ...props }, ref) => {
    const selectId = id ?? label?.toLowerCase().replace(/\s+/g, '-')

    return (
      <div className="flex flex-col gap-1.5">
        {label && (
          <label htmlFor={selectId} className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            {label}
            {props.required && <span className="text-blue-400 ml-0.5">*</span>}
          </label>
        )}

        <select
          ref={ref}
          id={selectId}
          className={cn(
            'w-full rounded-lg border bg-white text-sm text-slate-800',
            'px-3.5 py-2 h-9 pr-8 appearance-none',
            "bg-[url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24'%3E%3Cpath stroke='%2393C5FD' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M6 9l6 6 6-6'/%3E%3C/svg%3E\")] bg-no-repeat bg-[right_0.5rem_center] bg-[length:1.25rem]",
            'transition-all duration-normal',
            'focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-400',
            'disabled:bg-blue-50/50 disabled:text-slate-400 disabled:cursor-not-allowed',
            error ? 'border-err/50' : 'border-blue-100 hover:border-blue-200',
            className,
          )}
          {...props}
        >
          {placeholder && <option value="" disabled>{placeholder}</option>}
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
          ))}
        </select>

        {error ? (
          <p className="text-xs text-err">⚠ {error}</p>
        ) : hint ? (
          <p className="text-xs text-slate-400">{hint}</p>
        ) : null}
      </div>
    )
  },
)

Select.displayName = 'Select'

// ─── Textarea ─────────────────────────────────────────────────────────────────

interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  hint?: string
  error?: string
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ label, hint, error, className, id, ...props }, ref) => {
    const textareaId = id ?? label?.toLowerCase().replace(/\s+/g, '-')

    return (
      <div className="flex flex-col gap-1.5">
        {label && (
          <label htmlFor={textareaId} className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            {label}
            {props.required && <span className="text-blue-400 ml-0.5">*</span>}
          </label>
        )}

        <textarea
          ref={ref}
          id={textareaId}
          rows={3}
          className={cn(
            'w-full rounded-lg border bg-white text-sm text-slate-800',
            'px-3.5 py-2.5 resize-y min-h-[80px]',
            'placeholder:text-slate-300',
            'transition-all duration-normal',
            'focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-400',
            'disabled:bg-blue-50/50 disabled:text-slate-400 disabled:cursor-not-allowed',
            error ? 'border-err/50' : 'border-blue-100 hover:border-blue-200',
            className,
          )}
          {...props}
        />

        {error ? (
          <p className="text-xs text-err">⚠ {error}</p>
        ) : hint ? (
          <p className="text-xs text-slate-400">{hint}</p>
        ) : null}
      </div>
    )
  },
)

Textarea.displayName = 'Textarea'
