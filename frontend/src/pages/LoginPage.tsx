import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Eye, EyeOff, ArrowRight } from 'lucide-react'
import { Button, Input, Alert } from '@/components/ui'
import { useAuth } from '@/context/AuthContext'

export function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    const formData = new FormData(e.currentTarget)
    const email = (formData.get('email') as string).trim()
    const password = formData.get('password') as string

    if (!email) {
      setError('El correo electrónico es obligatorio')
      setLoading(false)
      return
    }

    if (!password) {
      setError('La contraseña es obligatoria')
      setLoading(false)
      return
    }

    try {
      await login(email, password)
      navigate('/dashboard', { replace: true })
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) {
        setError(data[0] ?? 'Error al iniciar sesión')
      } else if (typeof data === 'string') {
        setError(data)
      } else {
        setError('Error al iniciar sesión. Intente de nuevo.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-dvh bg-blue-950 flex">
      {/* Left panel — decorative */}
      <div className="hidden lg:flex flex-col justify-between w-[420px] p-12 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-blue-600/10 via-transparent to-transparent pointer-events-none" />
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-blue-500/5 rounded-full blur-3xl" />
        <div className="absolute -bottom-32 -right-32 w-96 h-96 bg-blue-400/5 rounded-full blur-3xl" />

        {/* Brand */}
        <div className="relative">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white shadow-inner-top">
              iQ
            </div>
            <span className="text-lg font-bold text-white">Fleet</span>
          </div>
        </div>

        {/* Copy */}
        <div className="relative space-y-4">
          <h2 className="text-3xl font-bold text-white leading-snug">
            Gestión de flota<br />
            <span className="text-blue-400">inteligente</span>
          </h2>
          <p className="text-sm text-slate-400 leading-relaxed">
            Controla vehículos, conductores y documentos de tu flota desde un solo lugar.
          </p>

          <div className="flex flex-col gap-2 pt-2">
            {['Alertas de vencimiento automáticas', 'Balance financiero en tiempo real', 'Gestión documental centralizada'].map((f) => (
              <div key={f} className="flex items-center gap-2 text-xs text-slate-400">
                <div className="w-1.5 h-1.5 rounded-full bg-blue-500 shrink-0" />
                {f}
              </div>
            ))}
          </div>
        </div>

        <p className="relative text-xs text-slate-600">Universidad Popular del Cesar · 2026</p>
      </div>

      {/* Right panel — form */}
      <div className="flex-1 flex items-center justify-center p-6 bg-surface-page">
        <div className="w-full max-w-sm">
          {/* Mobile brand */}
          <div className="flex items-center gap-2.5 mb-8 lg:hidden">
            <div className="w-8 h-8 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white text-sm">
              iQ
            </div>
            <span className="font-bold text-slate-800">iQFleet</span>
          </div>

          <div className="mb-7">
            <h1 className="text-xl font-bold text-slate-800 tracking-tight">Bienvenido</h1>
            <p className="text-sm text-slate-400 mt-1">Ingresa tus credenciales para continuar</p>
          </div>

          {error && (
            <div className="mb-4">
              <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Correo electrónico"
              name="email"
              type="email"
              autoComplete="email"
              required
              placeholder="usuario@empresa.co"
            />

            <Input
              label="Contraseña"
              name="password"
              type={showPassword ? 'text' : 'password'}
              autoComplete="current-password"
              required
              placeholder="••••••••"
              trailingIcon={
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  className="pointer-events-auto text-slate-300 hover:text-blue-400 transition-colors"
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
              }
            />

            <Button
              type="submit"
              loading={loading}
              icon={<ArrowRight size={14} />}
              iconPosition="right"
              size="lg"
              className="w-full mt-2"
            >
              Ingresar
            </Button>
          </form>

          <div className="mt-6 space-y-2 text-center">
            <p className="text-xs text-slate-400">
              <Link to="/forgot-password" className="text-blue-500 hover:text-blue-400 font-medium transition-colors">
                ¿Olvidaste tu contraseña?
              </Link>
            </p>
            <p className="text-xs text-slate-400">
              ¿No tienes acceso?{' '}
              <Link to="/register" className="text-blue-500 hover:text-blue-400 font-medium transition-colors">
                Solicitar registro
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
