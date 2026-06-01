import { http } from '@/lib/http'

export interface ResumenFlota {
  totalVehiculos: number
  vehiculosActivos: number
  vehiculosEnMantenimiento: number
  vehiculosInactivos: number
  totalConductores: number
  conductoresActivos: number
  conductoresInactivos: number
  totalDocumentos: number
}

export interface MovimientoReciente {
  id: string
  tipo: 'INCOME' | 'EXPENSE'
  monto: number
  fecha: string
  concepto: string
  vehiculoPlaca: string
}

export interface ResumenFinanciero {
  totalIngresos: number
  totalEgresos: number
  balanceGeneral: number
  periodoDesde: string
  periodoHasta: string
  movimientosRecientes: MovimientoReciente[]
}

export interface AlertaDocumento {
  documentoId: string
  nombre: string
  tipo: string
  fechaVencimiento: string
  diasRestantes: number
  estado: 'EXPIRED' | 'EXPIRING_SOON'
  vehiculoPlaca: string
  conductorNombre: string
}

export interface AlertaConductor {
  conductorId: string
  nombre: string
  numeroLicencia: string
  fechaVencimientoLicencia: string
  diasRestantes: number
}

export interface AlertasActivas {
  documentosProximosAVencer: AlertaDocumento[]
  documentosVencidos: AlertaDocumento[]
  conductoresLicenciaProxima: AlertaConductor[]
}

export interface UltimoAcceso {
  usuarioId: string
  nombre: string
  rol: string
  ultimoAcceso: string
}

export interface PanelAdmin {
  totalUsuarios: number
  usuariosActivos: number
  usuariosInactivos: number
  usuariosBloqueados: number
  solicitudesPendientes: number
  ultimosAccesos: UltimoAcceso[]
}

export async function getResumenFlota(): Promise<ResumenFlota> {
  const res = await http.get<ResumenFlota>('/dashboard/resumen-flota')
  return res.data
}

export async function getResumenFinanciero(
  fechaDesde?: string,
  fechaHasta?: string,
): Promise<ResumenFinanciero> {
  const params = new URLSearchParams()
  if (fechaDesde) params.set('fechaDesde', fechaDesde)
  if (fechaHasta) params.set('fechaHasta', fechaHasta)
  const q = params.toString()
  const res = await http.get<ResumenFinanciero>(`/dashboard/resumen-financiero${q ? `?${q}` : ''}`)
  return res.data
}

export async function getAlertasActivas(): Promise<AlertasActivas> {
  const res = await http.get<AlertasActivas>('/dashboard/alertas')
  return res.data
}

export async function getPanelAdmin(): Promise<PanelAdmin> {
  const res = await http.get<PanelAdmin>('/dashboard/panel-admin')
  return res.data
}
