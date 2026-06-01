import { useEffect, useState } from 'react'
import { Truck, Users, FileText, AlertTriangle, TrendingUp, TrendingDown, ShieldCheck } from 'lucide-react'
import { Page } from '@/components/layout'
import { StatCard, Card, Badge } from '@/components/ui'
import { formatCurrency } from '@/lib/utils'
import {
  getResumenFlota,
  getResumenFinanciero,
  getAlertasActivas,
  getPanelAdmin,
  type ResumenFlota,
  type ResumenFinanciero,
  type AlertasActivas,
  type PanelAdmin,
} from '@/services/dashboard'

export function DashboardPage() {
  const [flota, setFlota]           = useState<ResumenFlota | null>(null)
  const [financiero, setFinanciero] = useState<ResumenFinanciero | null>(null)
  const [alertas, setAlertas]       = useState<AlertasActivas | null>(null)
  const [panel, setPanel]           = useState<PanelAdmin | null>(null)
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState<string | null>(null)

  useEffect(() => {
    setLoading(true)
    Promise.all([
      getResumenFlota(),
      getResumenFinanciero(),
      getAlertasActivas(),
      getPanelAdmin(),
    ])
      .then(([f, fin, al, pa]) => {
        setFlota(f)
        setFinanciero(fin)
        setAlertas(al)
        setPanel(pa)
      })
      .catch(() => setError('No se pudieron cargar los datos del dashboard.'))
      .finally(() => setLoading(false))
  }, [])

  const totalAlertas =
    (alertas?.documentosProximosAVencer.length ?? 0) +
    (alertas?.documentosVencidos.length ?? 0) +
    (alertas?.conductoresLicenciaProxima.length ?? 0)

  const allAlertDocs = [
    ...(alertas?.documentosVencidos ?? []),
    ...(alertas?.documentosProximosAVencer ?? []),
  ].slice(0, 6)

  if (loading) {
    return (
      <Page title="Dashboard" subtitle="Estado general de la flota">
        <div className="flex items-center justify-center h-48 text-slate-400 text-sm">
          Cargando datos...
        </div>
      </Page>
    )
  }

  if (error) {
    return (
      <Page title="Dashboard" subtitle="Estado general de la flota">
        <div className="flex items-center justify-center h-48 text-err text-sm">{error}</div>
      </Page>
    )
  }

  return (
    <Page title="Dashboard" subtitle="Estado general de la flota">

      {/* Stat cards — flota */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        <StatCard
          label="Vehículos activos"
          value={flota?.vehiculosActivos ?? 0}
          variant="default"
          icon={<Truck size={18} />}
        />
        <StatCard
          label="Conductores activos"
          value={flota?.conductoresActivos ?? 0}
          variant="default"
          icon={<Users size={18} />}
        />
        <StatCard
          label="Docs. por vencer"
          value={alertas?.documentosProximosAVencer.length ?? 0}
          variant="warning"
          icon={<AlertTriangle size={18} />}
        />
        <StatCard
          label="Docs. vencidos"
          value={alertas?.documentosVencidos.length ?? 0}
          variant="danger"
          icon={<FileText size={18} />}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

        {/* Alertas de vencimiento */}
        <div className="lg:col-span-2">
          <Card>
            <Card.Header
              title="Alertas de vencimiento"
              subtitle="Documentos próximos a vencer y vencidos"
              action={
                totalAlertas > 0
                  ? <Badge variant="warning" dot>{totalAlertas} pendientes</Badge>
                  : <Badge variant="success">Sin alertas</Badge>
              }
            />

            {allAlertDocs.length === 0 && (
              <p className="text-xs text-slate-400 text-center py-6">No hay alertas activas</p>
            )}

            <div className="space-y-2.5">
              {allAlertDocs.map((a) => {
                const expired = a.estado === 'EXPIRED'
                const color = expired
                  ? 'text-err  bg-err-bg  border-err-border'
                  : 'text-warn bg-warn-bg border-warn-border'
                const label = a.vehiculoPlaca
                  ? `${a.nombre} · ${a.vehiculoPlaca}`
                  : a.conductorNombre
                  ? `${a.nombre} · ${a.conductorNombre}`
                  : a.nombre

                return (
                  <div
                    key={a.documentoId}
                    className={`flex items-center justify-between px-4 py-3 rounded-xl border ${color}`}
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <FileText size={14} className="shrink-0 opacity-60" />
                      <p className="text-sm font-medium truncate">{label}</p>
                    </div>
                    <span className="text-xs font-semibold shrink-0 ml-3">
                      {expired ? `${Math.abs(a.diasRestantes)}d vencido` : `${a.diasRestantes}d`}
                    </span>
                  </div>
                )
              })}

              {(alertas?.conductoresLicenciaProxima ?? []).slice(0, 3).map((c) => (
                <div
                  key={c.conductorId}
                  className="flex items-center justify-between px-4 py-3 rounded-xl border text-warn bg-warn-bg border-warn-border"
                >
                  <div className="flex items-center gap-3 min-w-0">
                    <Users size={14} className="shrink-0 opacity-60" />
                    <p className="text-sm font-medium truncate">Licencia · {c.nombre}</p>
                  </div>
                  <span className="text-xs font-semibold shrink-0 ml-3">
                    {c.diasRestantes >= 0 ? `${c.diasRestantes}d` : `${Math.abs(c.diasRestantes)}d vencida`}
                  </span>
                </div>
              ))}
            </div>
          </Card>
        </div>

        {/* Resumen financiero */}
        <Card>
          <Card.Header title="Resumen financiero" subtitle="Mes actual" />

          <div className="space-y-3">
            <div className="flex items-center justify-between p-3 rounded-xl bg-ok-bg border border-ok-border">
              <div className="flex items-center gap-2">
                <TrendingUp size={14} className="text-ok" />
                <span className="text-xs font-medium text-ok">Ingresos</span>
              </div>
              <span className="text-sm font-bold text-ok">
                {formatCurrency(financiero?.totalIngresos ?? 0)}
              </span>
            </div>

            <div className="flex items-center justify-between p-3 rounded-xl bg-err-bg border border-err-border">
              <div className="flex items-center gap-2">
                <TrendingDown size={14} className="text-err" />
                <span className="text-xs font-medium text-err">Egresos</span>
              </div>
              <span className="text-sm font-bold text-err">
                {formatCurrency(financiero?.totalEgresos ?? 0)}
              </span>
            </div>

            <div className="pt-1 border-t border-blue-100 flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500">Balance neto</span>
              <span className="text-xl font-bold text-blue-600 tracking-tight">
                {formatCurrency(financiero?.balanceGeneral ?? 0)}
              </span>
            </div>
          </div>

          {(financiero?.movimientosRecientes?.length ?? 0) > 0 && (
            <div className="mt-4 pt-4 border-t border-blue-50 space-y-1.5">
              <p className="text-xs font-semibold text-slate-400 mb-2">Movimientos recientes</p>
              {financiero!.movimientosRecientes.map((m) => (
                <div key={m.id} className="flex items-center justify-between gap-2">
                  <span className="text-xs text-slate-500 truncate max-w-[120px]">{m.concepto}</span>
                  <span className={`text-xs font-semibold ${m.tipo === 'INCOME' ? 'text-ok' : 'text-err'}`}>
                    {m.tipo === 'INCOME' ? '+' : '-'}{formatCurrency(m.monto)}
                  </span>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {/* Sección inferior: totalizadores flota + panel admin */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">

        {/* Totalizadores operativos */}
        <Card>
          <Card.Header title="Totalizadores de flota" subtitle="Estado operativo general" />
          <div className="grid grid-cols-2 gap-3">
            <StatItem label="Total vehículos"     value={flota?.totalVehiculos ?? 0} />
            <StatItem label="En mantenimiento"    value={flota?.vehiculosEnMantenimiento ?? 0} color="text-warn" />
            <StatItem label="Vehículos inactivos" value={flota?.vehiculosInactivos ?? 0} color="text-slate-400" />
            <StatItem label="Total conductores"   value={flota?.totalConductores ?? 0} />
            <StatItem label="Cond. inactivos"     value={flota?.conductoresInactivos ?? 0} color="text-slate-400" />
            <StatItem label="Total documentos"    value={flota?.totalDocumentos ?? 0} />
          </div>
        </Card>

        {/* Panel administrativo (usuarios) */}
        <Card>
          <Card.Header title="Panel administrativo" subtitle="Estado de usuarios" />
          <div className="grid grid-cols-2 gap-3 mb-4">
            <StatItem label="Total usuarios"      value={panel?.totalUsuarios ?? 0} />
            <StatItem label="Usuarios activos"    value={panel?.usuariosActivos ?? 0} color="text-ok" />
            <StatItem label="Inactivos"           value={panel?.usuariosInactivos ?? 0} color="text-slate-400" />
            <StatItem label="Bloqueados"          value={panel?.usuariosBloqueados ?? 0} color="text-err" />
          </div>

          {(panel?.solicitudesPendientes ?? 0) > 0 && (
            <div className="flex items-center justify-between px-3 py-2.5 rounded-xl bg-warn-bg border border-warn-border">
              <div className="flex items-center gap-2">
                <ShieldCheck size={14} className="text-warn" />
                <span className="text-xs font-medium text-warn">Solicitudes pendientes</span>
              </div>
              <span className="text-sm font-bold text-warn">{panel?.solicitudesPendientes}</span>
            </div>
          )}

          {(panel?.ultimosAccesos?.length ?? 0) > 0 && (
            <div className="mt-4 pt-3 border-t border-blue-50 space-y-1.5">
              <p className="text-xs font-semibold text-slate-400 mb-2">Últimos accesos</p>
              {panel!.ultimosAccesos.map((a) => (
                <div key={a.usuarioId} className="flex items-center justify-between gap-2">
                  <span className="text-xs text-slate-600 truncate">{a.nombre}</span>
                  <span className="text-xs text-slate-400 shrink-0">
                    {new Date(a.ultimoAcceso).toLocaleDateString('es-CO', { month: 'short', day: 'numeric' })}
                  </span>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </Page>
  )
}

function StatItem({
  label,
  value,
  color = 'text-slate-800',
}: {
  label: string
  value: number
  color?: string
}) {
  return (
    <div className="flex items-center justify-between px-3 py-2 rounded-xl bg-slate-50">
      <span className="text-xs text-slate-500 truncate">{label}</span>
      <span className={`text-sm font-bold ${color}`}>{value}</span>
    </div>
  )
}
