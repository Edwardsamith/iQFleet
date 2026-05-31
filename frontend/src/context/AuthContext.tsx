import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import {
  login as authLogin,
  logout as authLogout,
  getProfile,
  isAuthenticated,
  getToken,
  LoginResponse,
  UserProfile,
} from '@/lib/auth'

interface AuthUser extends UserProfile {
  token: string
}

interface AuthContextType {
  user: AuthUser | null
  isLoading: boolean
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<LoginResponse>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const initAuth = async () => {
      if (isAuthenticated()) {
        try {
          const profile = await getProfile()
          setUser({ ...profile, token: getToken()! })
        } catch {
          localStorage.removeItem('iqfleet_token')
        }
      }
      setIsLoading(false)
    }
    initAuth()
  }, [])

  const login = async (email: string, password: string): Promise<LoginResponse> => {
    const data = await authLogin(email, password)
    const profile = await getProfile()
    setUser({ ...profile, token: data.token })
    return data
  }

  const logout = async () => {
    await authLogout()
    setUser(null)
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: user !== null,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return context
}
