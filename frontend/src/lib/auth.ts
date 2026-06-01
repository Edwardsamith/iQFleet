import { http } from './http'

const TOKEN_KEY = 'iqfleet_token'

export interface LoginResponse {
  token: string
  role: string
  username: string
  email: string
  firstName: string
  lastName: string
}

export interface UserProfile {
  id: string
  firstName: string
  lastName: string
  email: string
  username: string
  phone?: string
  role: string
  status: string
  lastAccess?: string
}

interface JwtPayload {
  sub: string
  rol: string
  userId: string
  exp: number
}

function parseJwt(token: string): JwtPayload {
  const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
  const json = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join(''),
  )
  return JSON.parse(json)
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function isAuthenticated(): boolean {
  const token = getToken()
  if (!token) return false
  try {
    const payload = parseJwt(token)
    return payload.exp * 1000 > Date.now()
  } catch {
    return false
  }
}

export function getTokenPayload(): JwtPayload | null {
  const token = getToken()
  if (!token) return null
  try {
    return parseJwt(token)
  } catch {
    return null
  }
}

export async function login(email: string, password: string): Promise<LoginResponse> {
  const response = await http.post<LoginResponse>('/auth/login', { email, password })
  localStorage.setItem(TOKEN_KEY, response.data.token)
  return response.data
}

export interface RegisterPayload {
  firstName: string
  lastName: string
  identificationType: string
  identificationNumber: string
  email: string
  phone?: string
  username: string
  password: string
  role: string
}

export interface RegisterResponse {
  userId: string
  username: string
  message: string
}

export async function register(payload: RegisterPayload): Promise<RegisterResponse> {
  const response = await http.post<RegisterResponse>('/auth/registro', payload)
  return response.data
}

export async function logout(): Promise<void> {
  try {
    await http.post('/auth/logout')
  } finally {
    localStorage.removeItem(TOKEN_KEY)
  }
}

export async function getProfile(): Promise<UserProfile> {
  const response = await http.get<UserProfile>('/auth/me')
  return response.data
}

export async function requestPasswordRecovery(recipient: string): Promise<{ message: string }> {
  const response = await http.post<{ message: string }>('/auth/recover-password', {
    method: 'EMAIL',
    recipient,
  })
  return response.data
}

export async function verifyRecoveryCode(
  email: string,
  code: string,
): Promise<{ resetToken: string }> {
  const response = await http.post<{ resetToken: string }>('/auth/verify-code', { email, code })
  return response.data
}

export async function resetPassword(
  resetToken: string,
  newPassword: string,
): Promise<{ message: string }> {
  const response = await http.post<{ message: string }>('/auth/reset-password', {
    resetToken,
    newPassword,
  })
  return response.data
}
