import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { ArrowLeft, ArrowRight, Eye, EyeOff, CheckCircle } from 'lucide-react'
import { Button, Input, Alert } from '@/components/ui'
import { resetPassword } from '@/lib/auth'

export function ResetPasswordPage() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const resetToken = params.get('token') ?? ''

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    const formData = new FormData(e.currentTarget)
    const newPassword = formData.get('password') as string
    const confirmation = formData.get('confirm') as string

    if (!newPassword) {
      setError('La contraseña es obligatoria')
      setLoading(false)
      return
    }
    if (newPassword !== confirmation) {
      setError('Las contraseñas no coinciden')
      setLoading(false)
      return
    }
    if (newPassword.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres')
      setLoading(false)
      return
    }
    if (!/[A-Z]/.test(newPassword)) {
      setError('La contraseña debe contener al menos una letra mayúscula')
      setLoading(false)
      return
    }
    if (!/[0-9]/.test(newPassword)) {
      setError('La contraseña debe contener al menos un número')
      setLoading(false)
      return
    }

    try {
      await resetPassword(resetToken, newPassword)
      setSuccess(true)
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al restablecer la contraseña. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-dvh bg-blue-950 flex items-center justify-center p-6">
      <div className="w-full max-w-sm bg-surface-page rounded-2xl p-8 shadow-xl">
        <div className="flex items-center gap-2.5 mb-6">
          <div className="w-8 h-8 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white text-sm">
            iQ
          </div>
          <span className="font-bold text-slate-800">iQFleet</span>
        </div>

        {success ? (
          <div className="space-y-4 text-center">
            <div className="w-12 h-12 rounded-xl bg-green-50 flex items-center justify-center mx-auto">
              <CheckCircle size={24} className="text-green-600" />
            </div>
            <h1 className="text-lg font-bold text-slate-800">¡Contraseña actualizada!</h1>
            <p className="text-sm text-slate-400">
              Ya puedes iniciar sesión con tu nueva contraseña.
            </p>
            <Button
              className="w-full"
              size="lg"
              icon={<ArrowRight size={14} />}
              iconPosition="right"
              onClick={() => navigate('/login')}
            >
              Ir al inicio de sesión
            </Button>
          </div>
        ) : (
          <>
            <div className="mb-6">
              <h1 className="text-xl font-bold text-slate-800 tracking-tight">Nueva contraseña</h1>
              <p className="text-sm text-slate-400 mt-1">
                Elige una contraseña segura para tu cuenta.
              </p>
            </div>

            {error && (
              <div className="mb-4">
                <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <Input
                label="Nueva contraseña"
                name="password"
                type={showPassword ? 'text' : 'password'}
                required
                placeholder="Mín. 8 caracteres, 1 mayúscula, 1 número"
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
              <Input
                label="Confirmar contraseña"
                name="confirm"
                type={showConfirm ? 'text' : 'password'}
                required
                placeholder="Repite la contraseña"
                trailingIcon={
                  <button
                    type="button"
                    onClick={() => setShowConfirm((v) => !v)}
                    className="pointer-events-auto text-slate-300 hover:text-blue-400 transition-colors"
                    tabIndex={-1}
                  >
                    {showConfirm ? <EyeOff size={14} /> : <Eye size={14} />}
                  </button>
                }
              />

              <ul className="text-xs text-slate-400 space-y-0.5 pt-1">
                <li>• Mínimo 8 caracteres</li>
                <li>• Al menos 1 letra mayúscula</li>
                <li>• Al menos 1 número</li>
              </ul>

              <Button
                type="submit"
                loading={loading}
                icon={<ArrowRight size={14} />}
                iconPosition="right"
                size="lg"
                className="w-full mt-2"
              >
                Restablecer contraseña
              </Button>
            </form>

            <p className="mt-5 text-center text-xs text-slate-400">
              <Link
                to="/login"
                className="flex items-center justify-center gap-1 text-blue-500 hover:text-blue-400 font-medium transition-colors"
              >
                <ArrowLeft size={12} />
                Volver al inicio de sesión
              </Link>
            </p>
          </>
        )}
      </div>
    </div>
  )
}
