import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Eye, EyeOff, ArrowRight, CheckCircle } from 'lucide-react'
import { Button, Input, Alert, Select } from '@/components/ui'
import { register } from '@/lib/auth'

const ID_TYPES = [
  { value: 'CC', label: 'Cédula de Ciudadanía (CC)' },
  { value: 'CE', label: 'Cédula de Extranjería (CE)' },
  { value: 'PA', label: 'Pasaporte (PA)' },
]

const ROLES = [
  { value: 'ROLE_OWNER', label: 'Propietario' },
  { value: 'ROLE_ADMIN', label: 'Administrador' },
]

export function RegisterPage() {
  const navigate = useNavigate()
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    const fd = new FormData(e.currentTarget)
    const firstName = (fd.get('firstName') as string).trim()
    const lastName = (fd.get('lastName') as string).trim()
    const identificationType = fd.get('identificationType') as string
    const identificationNumber = (fd.get('identificationNumber') as string).trim()
    const email = (fd.get('email') as string).trim()
    const phone = (fd.get('phone') as string).trim()
    const username = (fd.get('username') as string).trim()
    const password = fd.get('password') as string
    const role = fd.get('role') as string

    const clientErrors: string[] = []
    if (!firstName) clientErrors.push('El nombre es obligatorio')
    if (!lastName) clientErrors.push('El apellido es obligatorio')
    if (!identificationType) clientErrors.push('El tipo de identificación es obligatorio')
    if (!identificationNumber) clientErrors.push('El número de identificación es obligatorio')
    if (!email) clientErrors.push('El correo electrónico es obligatorio')
    if (!username) clientErrors.push('El nombre de usuario es obligatorio')
    if (!password) clientErrors.push('La contraseña es obligatoria')
    else if (password.length < 8) clientErrors.push('La contraseña debe tener mínimo 8 caracteres')
    else if (!/[A-Z]/.test(password)) clientErrors.push('La contraseña debe tener al menos una mayúscula')
    else if (!/[0-9]/.test(password)) clientErrors.push('La contraseña debe tener al menos un número')
    if (!role) clientErrors.push('El rol es obligatorio')

    if (clientErrors.length > 0) {
      setError(clientErrors[0])
      setLoading(false)
      return
    }

    try {
      await register({
        firstName,
        lastName,
        identificationType,
        identificationNumber,
        email,
        phone: phone || undefined,
        username,
        password,
        role,
      })
      setSuccess(true)
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) {
        setError(data[0] ?? 'Error al enviar solicitud')
      } else if (typeof data === 'string') {
        setError(data)
      } else {
        setError('Error al enviar solicitud. Intente de nuevo.')
      }
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <div className="min-h-dvh bg-surface-page flex items-center justify-center p-6">
        <div className="w-full max-w-sm text-center space-y-5">
          <div className="flex justify-center">
            <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center">
              <CheckCircle size={32} className="text-green-600" />
            </div>
          </div>
          <div>
            <h1 className="text-xl font-bold text-slate-800">Solicitud enviada</h1>
            <p className="text-sm text-slate-500 mt-2">
              Tu solicitud de acceso está pendiente de revisión por un administrador. Recibirás respuesta pronto.
            </p>
          </div>
          <Button variant="ghost" className="w-full" onClick={() => navigate('/login')}>
            Volver al inicio de sesión
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-dvh bg-blue-950 flex">
      {/* Left panel */}
      <div className="hidden lg:flex flex-col justify-between w-[380px] p-12 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-blue-600/10 via-transparent to-transparent pointer-events-none" />
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-blue-500/5 rounded-full blur-3xl" />
        <div className="absolute -bottom-32 -right-32 w-96 h-96 bg-blue-400/5 rounded-full blur-3xl" />

        <div className="relative flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white shadow-inner-top">
            iQ
          </div>
          <span className="text-lg font-bold text-white">Fleet</span>
        </div>

        <div className="relative space-y-3">
          <h2 className="text-2xl font-bold text-white leading-snug">
            Solicita acceso a<br />
            <span className="text-blue-400">iQFleet</span>
          </h2>
          <p className="text-sm text-slate-400 leading-relaxed">
            Completa el formulario y un administrador revisará tu solicitud para habilitarte el acceso.
          </p>
        </div>

        <p className="relative text-xs text-slate-600">Universidad Popular del Cesar · 2026</p>
      </div>

      {/* Right panel — form */}
      <div className="flex-1 flex items-start justify-center p-6 bg-surface-page overflow-y-auto">
        <div className="w-full max-w-md py-8">
          {/* Mobile brand */}
          <div className="flex items-center gap-2.5 mb-6 lg:hidden">
            <div className="w-8 h-8 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white text-sm">
              iQ
            </div>
            <span className="font-bold text-slate-800">iQFleet</span>
          </div>

          <div className="mb-6">
            <h1 className="text-xl font-bold text-slate-800 tracking-tight">Solicitar registro</h1>
            <p className="text-sm text-slate-400 mt-1">Completa todos los campos para enviar tu solicitud de acceso</p>
          </div>

          {error && (
            <div className="mb-4">
              <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Nombres y apellidos */}
            <div className="grid grid-cols-2 gap-3">
              <Input
                label="Nombres"
                name="firstName"
                autoComplete="given-name"
                required
                placeholder="Juan"
                pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}"
                title="Solo letras y espacios, entre 2 y 100 caracteres"
              />
              <Input
                label="Apellidos"
                name="lastName"
                autoComplete="family-name"
                required
                placeholder="Pérez"
                pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}"
                title="Solo letras y espacios, entre 2 y 100 caracteres"
              />
            </div>

            {/* Identificación */}
            <div className="grid grid-cols-2 gap-3">
              <Select
                label="Tipo ID"
                name="identificationType"
                required
                placeholder="Selecciona"
                options={ID_TYPES}
                defaultValue=""
              />
              <Input
                label="Número ID"
                name="identificationNumber"
                required
                placeholder="1234567890"
                pattern="[0-9]{6,12}"
                title="Entre 6 y 12 dígitos"
                inputMode="numeric"
              />
            </div>

            {/* Correo */}
            <Input
              label="Correo electrónico"
              name="email"
              type="email"
              autoComplete="email"
              required
              placeholder="usuario@empresa.co"
            />

            {/* Teléfono */}
            <Input
              label="Teléfono"
              name="phone"
              type="tel"
              autoComplete="tel"
              placeholder="3001234567"
              pattern="[0-9]{7,15}"
              title="Entre 7 y 15 dígitos"
              inputMode="numeric"
            />

            {/* Username */}
            <Input
              label="Nombre de usuario"
              name="username"
              autoComplete="username"
              required
              placeholder="juan.perez"
              pattern="[a-zA-Z0-9_.]{4,30}"
              title="4-30 caracteres. Solo letras, números, punto y guión bajo"
            />

            {/* Contraseña */}
            <Input
              label="Contraseña"
              name="password"
              type={showPassword ? 'text' : 'password'}
              autoComplete="new-password"
              required
              placeholder="••••••••"
              hint="Mínimo 8 caracteres, 1 mayúscula y 1 número"
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

            {/* Rol */}
            <Select
              label="Rol solicitado"
              name="role"
              required
              placeholder="Selecciona un rol"
              options={ROLES}
              defaultValue=""
            />

            <Button
              type="submit"
              loading={loading}
              icon={<ArrowRight size={14} />}
              iconPosition="right"
              size="lg"
              className="w-full mt-2"
            >
              Enviar solicitud
            </Button>
          </form>

          <p className="mt-6 text-center text-xs text-slate-400">
            ¿Ya tienes acceso?{' '}
            <Link to="/login" className="text-blue-500 hover:text-blue-400 font-medium transition-colors">
              Iniciar sesión
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
