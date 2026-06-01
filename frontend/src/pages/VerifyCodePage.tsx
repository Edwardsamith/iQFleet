import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { ArrowLeft, ArrowRight, RefreshCw } from 'lucide-react'
import { Button, Input, Alert } from '@/components/ui'
import { verifyRecoveryCode, requestPasswordRecovery } from '@/lib/auth'

export function VerifyCodePage() {
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const email = params.get('email') ?? ''

  const [loading, setLoading] = useState(false)
  const [resending, setResending] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    const formData = new FormData(e.currentTarget)
    const code = (formData.get('code') as string).trim()

    if (!code || code.length !== 6) {
      setError('Ingresa el código de 6 dígitos')
      setLoading(false)
      return
    }

    try {
      const { resetToken } = await verifyRecoveryCode(email, code)
      navigate(`/reset-password?token=${encodeURIComponent(resetToken)}`)
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Código inválido o expirado. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  const handleResend = async () => {
    if (!email) return
    setResending(true)
    setError(null)
    setSuccessMessage(null)
    try {
      await requestPasswordRecovery(email)
      setSuccessMessage('Se envió un nuevo código. Revisa tu correo.')
    } catch {
      setError('No se pudo reenviar el código. Intenta más tarde.')
    } finally {
      setResending(false)
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

        <div className="mb-6">
          <h1 className="text-xl font-bold text-slate-800 tracking-tight">Verificar código</h1>
          <p className="text-sm text-slate-400 mt-1">
            Ingresa el código de 6 dígitos enviado a{' '}
            <span className="font-medium text-slate-600">{email || 'tu correo'}</span>.
          </p>
        </div>

        {error && (
          <div className="mb-4">
            <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
          </div>
        )}
        {successMessage && (
          <div className="mb-4">
            <Alert variant="success" onClose={() => setSuccessMessage(null)}>{successMessage}</Alert>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Código de verificación"
            name="code"
            type="text"
            inputMode="numeric"
            maxLength={6}
            pattern="[0-9]{6}"
            required
            placeholder="123456"
            className="text-center tracking-widest text-lg font-mono"
          />
          <Button
            type="submit"
            loading={loading}
            icon={<ArrowRight size={14} />}
            iconPosition="right"
            size="lg"
            className="w-full mt-2"
          >
            Verificar código
          </Button>
        </form>

        <div className="mt-5 flex flex-col items-center gap-3 text-xs text-slate-400">
          <button
            type="button"
            onClick={handleResend}
            disabled={resending}
            className="flex items-center gap-1.5 text-blue-500 hover:text-blue-400 font-medium transition-colors disabled:opacity-50"
          >
            <RefreshCw size={12} className={resending ? 'animate-spin' : ''} />
            {resending ? 'Enviando…' : 'Reenviar código'}
          </button>
          <Link
            to="/login"
            className="flex items-center gap-1 text-slate-400 hover:text-blue-500 transition-colors"
          >
            <ArrowLeft size={12} />
            Volver al inicio de sesión
          </Link>
        </div>
      </div>
    </div>
  )
}
