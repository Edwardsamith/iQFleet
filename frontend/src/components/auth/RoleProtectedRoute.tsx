import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/context/AuthContext'

interface RoleProtectedRouteProps {
  allowedRoles: string[]
  redirectTo?: string
}

export function RoleProtectedRoute({
  allowedRoles,
  redirectTo = '/dashboard',
}: RoleProtectedRouteProps) {
  const { user, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div className="min-h-dvh flex items-center justify-center bg-surface-page">
        <div className="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (!user || !allowedRoles.includes(user.role)) {
    return <Navigate to={redirectTo} replace />
  }

  return <Outlet />
}
