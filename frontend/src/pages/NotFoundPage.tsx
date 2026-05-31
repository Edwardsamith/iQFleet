import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui'

export function NotFoundPage() {
  const navigate = useNavigate()
  return (
    <div className="min-h-dvh flex flex-col items-center justify-center text-center px-4">
      <p className="text-8xl font-bold text-brand-100">404</p>
      <h1 className="text-xl font-bold text-slate-900 mt-4">Página no encontrada</h1>
      <p className="text-sm text-slate-500 mt-2">La ruta que buscas no existe.</p>
      <Button className="mt-6" onClick={() => navigate('/dashboard')}>
        Volver al dashboard
      </Button>
    </div>
  )
}
