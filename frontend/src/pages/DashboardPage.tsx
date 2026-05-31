import { Truck, Users, FileText, AlertTriangle, TrendingUp, TrendingDown } from 'lucide-react'
import { Page } from '@/components/layout'
import { StatCard, Card, Badge } from '@/components/ui'
import { formatCurrency } from '@/lib/utils'

const stats = [
  { label: 'Vehículos activos',     value: 24, variant: 'default'  as const, icon: <Truck size={18} /> },
  { label: 'Conductores activos',   value: 31, variant: 'default'  as const, icon: <Users size={18} /> },
  { label: 'Documentos por vencer', value: 7,  variant: 'warning'  as const, icon: <AlertTriangle size={18} /> },
  { label: 'Documentos vencidos',   value: 2,  variant: 'danger'   as const, icon: <FileText size={18} /> },
]

const alerts = [
  { id: '1', name: 'SOAT · Bus ABC-123',  type: 'SOAT',     days: 5,  color: 'text-err  bg-err-bg  border-err-border' },
  { id: '2', name: 'Licencia · J. Pérez', type: 'LICENCIA', days: 12, color: 'text-warn bg-warn-bg border-warn-border' },
  { id: '3', name: 'RTM · Bus XYZ-456',   type: 'RTM',      days: 18, color: 'text-warn bg-warn-bg border-warn-border' },
]

const financial = { income: 12_500_000, expense: 4_200_000 }
const balance = financial.income - financial.expense

export function DashboardPage() {
  return (
    <Page title="Dashboard" subtitle="Estado general de la flota">

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {stats.map((s) => <StatCard key={s.label} {...s} />)}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

        {/* Alertas */}
        <div className="lg:col-span-2">
          <Card>
            <Card.Header
              title="Alertas de vencimiento"
              subtitle="Documentos próximos a vencer"
              action={<Badge variant="warning" dot>{alerts.length} pendientes</Badge>}
            />

            <div className="space-y-2.5">
              {alerts.map((a) => (
                <div
                  key={a.id}
                  className={`flex items-center justify-between px-4 py-3 rounded-xl border ${a.color}`}
                >
                  <div className="flex items-center gap-3 min-w-0">
                    <FileText size={14} className="shrink-0 opacity-60" />
                    <p className="text-sm font-medium truncate">{a.name}</p>
                  </div>
                  <span className="text-xs font-semibold shrink-0 ml-3">
                    {a.days}d
                  </span>
                </div>
              ))}
            </div>
          </Card>
        </div>

        {/* Financiero */}
        <Card>
          <Card.Header title="Resumen financiero" subtitle="Mes actual" />

          <div className="space-y-3">
            {/* Ingreso */}
            <div className="flex items-center justify-between p-3 rounded-xl bg-ok-bg border border-ok-border">
              <div className="flex items-center gap-2">
                <TrendingUp size={14} className="text-ok" />
                <span className="text-xs font-medium text-ok">Ingresos</span>
              </div>
              <span className="text-sm font-bold text-ok">
                {formatCurrency(financial.income)}
              </span>
            </div>

            {/* Egreso */}
            <div className="flex items-center justify-between p-3 rounded-xl bg-err-bg border border-err-border">
              <div className="flex items-center gap-2">
                <TrendingDown size={14} className="text-err" />
                <span className="text-xs font-medium text-err">Egresos</span>
              </div>
              <span className="text-sm font-bold text-err">
                {formatCurrency(financial.expense)}
              </span>
            </div>

            {/* Balance */}
            <div className="pt-1 border-t border-blue-100 flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500">Balance neto</span>
              <span className="text-xl font-bold text-blue-600 tracking-tight">
                {formatCurrency(balance)}
              </span>
            </div>
          </div>
        </Card>
      </div>
    </Page>
  )
}
