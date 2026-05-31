import type { Config } from 'tailwindcss'

const config: Config = {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        // Surface tokens — blue-tinted whites
        surface: {
          DEFAULT: '#FFFFFF',
          page:    '#EFF6FF', // blue-50
          subtle:  '#DBEAFE', // blue-100
        },
        // Semantic status — soft, desaturated
        ok: {
          DEFAULT: '#059669', // emerald-600
          bg:      '#ECFDF5', // emerald-50
          border:  '#A7F3D0', // emerald-200
        },
        warn: {
          DEFAULT: '#D97706', // amber-600
          bg:      '#FFFBEB', // amber-50
          border:  '#FDE68A', // amber-200
        },
        err: {
          DEFAULT: '#DC2626', // red-600
          bg:      '#FEF2F2', // red-50
          border:  '#FECACA', // red-200
        },
        muted: {
          DEFAULT: '#94A3B8', // slate-400
          bg:      '#F8FAFC', // slate-50
          border:  '#E2E8F0', // slate-200
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'Menlo', 'monospace'],
      },
      borderRadius: {
        'sm':  '6px',
        'md':  '10px',
        'lg':  '14px',
        'xl':  '18px',
        '2xl': '22px',
        '3xl': '28px',
      },
      boxShadow: {
        // Blue-tinted shadows — very soft
        'card':       '0 1px 3px rgb(37 99 235 / 0.06), 0 1px 2px rgb(37 99 235 / 0.04)',
        'card-hover': '0 4px 16px rgb(37 99 235 / 0.10), 0 1px 4px rgb(37 99 235 / 0.06)',
        'modal':      '0 24px 72px -12px rgb(15 23 42 / 0.22)',
        'inner-top':  'inset 0 1px 0 rgb(255 255 255 / 0.12)',
      },
      spacing: {
        '4.5': '1.125rem',
        '18':  '4.5rem',
        '72':  '18rem',
        '80':  '20rem',
      },
      transitionDuration: {
        'fast':   '100ms',
        'normal': '150ms',
        'slow':   '250ms',
      },
    },
  },
  plugins: [],
}

export default config
