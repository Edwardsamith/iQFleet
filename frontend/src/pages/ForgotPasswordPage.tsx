import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, ArrowRight, Mail } from 'lucide-react'
import { Button, Input, Alert } from '@/components/ui'
import { requestPasswordRecovery } from '@/lib/auth'

export function ForgotPasswordPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [sent, setSent] = useState(false)
  const [submittedEmail, setSubmittedEmail] = useState('')

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    const formData = new FormData(e.currentTarget)
    const email = (formData.get('email') as string).trim()

    if (!email) {
      setError('El correo electrónico es obligatorio')
      setLoading(false)
      return
    }

    try {
      await requestPasswordRecovery(email)
      setSubmittedEmail(email)
      setSent(true)
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al procesar la solicitud. Intenta de nuevo.')
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

        {sent ? (
          <div className="space-y-4">
            <div className="w-12 h-12 rounded-xl bg-blue-50 flex items-center justify-center mx-auto">
              <Mail size={22} className="text-blue-600" />
            </div>
            <div className="text-center">
              <h1 className="text-lg font-bold text-slate-800">Revisa tu correo</h1>
              <p className="text-sm text-slate-400 mt-1">
                Si <span className="font-medium text-slate-600">{submittedEmail}</span> está registrado,
                recibirás un código de verificación de 6 dígitos.
              </p>
            </div>
            <Button
              className="w-full"
              size="lg"
              icon={<ArrowRight size={14} />}
              iconPosition="right"
              onClick={() => navigate(`/verify-code?email=${encodeURIComponent(submittedEmail)}`)}
            >
              Ingresar código
            </Button>
            <p className="text-center text-xs text-slate-400">
              ¿No recibiste nada?{' '}
              <button
                type="button"
                onClick={() => setSent(false)}
                className="text-blue-500 hover:text-blue-400 font-medium transition-colors"
              >
                Volver a intentar
              </button>
            </p>
          </div>
        ) : (
          <>
            <div className="mb-6">
              <h1 className="text-xl font-bold text-slate-800 tracking-tight">
                Recuperar contraseña
              </h1>
              <p className="text-sm text-slate-400 mt-1">
                Ingresa tu correo y te enviaremos un código de verificación.
              </p>
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
              <Button
                type="submit"
                loading={loading}
                icon={<ArrowRight size={14} />}
                iconPosition="right"
                size="lg"
                className="w-full mt-2"
              >
                Enviar código
              </Button>
            </form>

            <p className="mt-6 text-center text-xs text-slate-400">
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
