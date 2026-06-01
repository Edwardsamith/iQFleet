import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from '@/context/AuthContext'
import { ProtectedRoute } from '@/components/auth/ProtectedRoute'
import { RoleProtectedRoute } from '@/components/auth/RoleProtectedRoute'
import { AppLayout } from '@/components/layout'
import { DashboardPage } from '@/pages/DashboardPage'
import { DriversPage } from '@/pages/DriversPage'
import { VehiclesPage } from '@/pages/VehiclesPage'
import { DocumentsPage } from '@/pages/DocumentsPage'
import { FinancesPage } from '@/pages/FinancesPage'
import { UsersPage } from '@/pages/UsersPage'
import { LoginPage } from '@/pages/LoginPage'
import { RegisterPage } from '@/pages/RegisterPage'
import { ForgotPasswordPage } from '@/pages/ForgotPasswordPage'
import { VerifyCodePage } from '@/pages/VerifyCodePage'
import { ResetPasswordPage } from '@/pages/ResetPasswordPage'
import { NotFoundPage } from '@/pages/NotFoundPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/verify-code" element={<VerifyCodePage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<AppLayout />}>
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route path="dashboard"  element={<DashboardPage />} />
              <Route path="drivers"    element={<DriversPage />} />
              <Route path="vehicles"   element={<VehiclesPage />} />
              <Route path="documents"  element={<DocumentsPage />} />
              <Route path="finances"   element={<FinancesPage />} />
              {/* RF-005: user management is OWNER-only */}
              <Route element={<RoleProtectedRoute allowedRoles={['ROLE_OWNER']} />}>
                <Route path="users" element={<UsersPage />} />
              </Route>
            </Route>
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
