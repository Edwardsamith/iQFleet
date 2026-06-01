import { useState, useCallback, useMemo } from 'react'
import {
  Plus, DollarSign, TrendingUp, TrendingDown, Search,
  RefreshCw, MoreHorizontal, Ban, BarChart2,
} from 'lucide-react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Page } from '@/components/layout'
import {
  Button, Card, Table, MovementTypeBadge, MovementStatusBadge,
  StatCard, Input, Select, Textarea, Modal, ConfirmModal, Alert, Badge,
} from '@/components/ui'
import { formatCurrency, formatDate } from '@/lib/utils'
import { useAuth } from '@/context/AuthContext'
import type {
  FinancialMovement, MovementType, MovementCategory, PaymentMethod, Vehicle,
} from '@/types/domain'
import {
  listMovements, registerMovement, cancelMovement, getFleetBalance,
  type RegisterMovementPayload, type MovementsFilter, type FleetBalance,
} from '@/services/finances'
import { listVehicles } from '@/services/vehicles'

// ─── Constants ────────────────────────────────────────────────────────────────

const CATEGORY_LABEL: Record<MovementCategory, string> = {
  DAILY_COLLECTION:       'Recaudo diario',
  SUBSIDY:                'Subsidio / incentivo',
  OTHER_INCOME:           'Otro ingreso',
  FUEL:                   'Combustible',
  PREVENTIVE_MAINTENANCE: 'Mant. preventivo',
  CORRECTIVE_MAINTENANCE: 'Mant. correctivo',
  SALARY:                 'Salarios y pagos',
  INSURANCE:              'Seguros',
  PAPERWORK:              'Documentos y trámites',
  TAX:                    'Impuestos y tasas',
  OTHER_EXPENSE:          'Otro egreso',
}

const INCOME_CATEGORIES: { value: MovementCategory; label: string }[] = [
  { value: 'DAILY_COLLECTION', label: 'Recaudo diario' },
  { value: 'SUBSIDY',          label: 'Subsidio / incentivo' },
  { value: 'OTHER_INCOME',     label: 'Otro ingreso' },
]

const EXPENSE_CATEGORIES: { value: MovementCategory; label: string }[] = [
  { value: 'FUEL',                   label: 'Combustible' },
  { value: 'PREVENTIVE_MAINTENANCE', label: 'Mant. preventivo' },
  { value: 'CORRECTIVE_MAINTENANCE', label: 'Mant. correctivo' },
  { value: 'SALARY',                 label: 'Salarios y pagos' },
  { value: 'INSURANCE',              label: 'Seguros' },
  { value: 'PAPERWORK',              label: 'Documentos y trámites' },
  { value: 'TAX',                    label: 'Impuestos y tasas' },
  { value: 'OTHER_EXPENSE',          label: 'Otro egreso' },
]

const PAYMENT_METHOD_LABEL: Record<PaymentMethod, string> = {
  CASH:          'Efectivo',
  BANK_TRANSFER: 'Transferencia',
}

const MOVEMENT_TYPE_FILTER_OPTIONS = [
  { value: '',        label: 'Todos los tipos' },
  { value: 'INCOME',  label: 'Ingresos' },
  { value: 'EXPENSE', label: 'Egresos' },
]

const PAYMENT_METHOD_FILTER_OPTIONS = [
  { value: '',              label: 'Todos los métodos' },
  { value: 'CASH',          label: 'Efectivo' },
  { value: 'BANK_TRANSFER', label: 'Transferencia' },
]

const today = new Date().toISOString().split('T')[0]
const firstOfMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
  .toISOString()
  .split('T')[0]

// ─── Register Movement Modal ──────────────────────────────────────────────────

interface RegisterFormValues {
  movementType: MovementType
  category: MovementCategory
  paymentMethod: PaymentMethod
  amount: string
  date: string
  description: string
  notes: string
  vehicleId: string
  driverId: string
}

const EMPTY_FORM: RegisterFormValues = {
  movementType: 'INCOME',
  category:     'DAILY_COLLECTION',
  paymentMethod: 'CASH',
  amount:        '',
  date:          today,
  description:   '',
  notes:         '',
  vehicleId:     '',
  driverId:      '',
}

interface RegisterModalProps {
  open: boolean
  onClose: () => void
  onSuccess: () => void
  vehicles: Vehicle[]
  registeredBy: string
}

function RegisterMovementModal({ open, onClose, onSuccess, vehicles, registeredBy }: RegisterModalProps) {
  const [form, setForm] = useState<RegisterFormValues>(EMPTY_FORM)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const setField =
    (field: keyof RegisterFormValues) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
      const value = e.target.value
      setForm((prev) => {
        const next = { ...prev, [field]: value }
        if (field === 'movementType') {
          next.category = value === 'INCOME' ? 'DAILY_COLLECTION' : 'FUEL'
        }
        return next
      })
    }

  const categoryOptions = form.movementType === 'INCOME' ? INCOME_CATEGORIES : EXPENSE_CATEGORIES

  const vehicleOptions = [
    { value: '', label: 'Selecciona un vehículo…' },
    ...vehicles.map((v) => ({ value: v.id, label: `${v.plateNumber} — ${v.brand} ${v.vehicleModel}` })),
  ]

  const handleSubmit = async () => {
    setError(null)

    if (!form.vehicleId) { setError('Debes seleccionar un vehículo.'); return }
    if (!form.amount || Number(form.amount) <= 0) { setError('El monto debe ser mayor a cero.'); return }
    if (!form.date) { setError('La fecha es obligatoria.'); return }

    setLoading(true)
    try {
      const payload: RegisterMovementPayload = {
        movementType:  form.movementType,
        category:      form.category,
        paymentMethod: form.paymentMethod,
        amount:        Number(form.amount),
        date:          form.date,
        description:   form.description || undefined,
        notes:         form.notes || undefined,
        vehicleId:     form.vehicleId,
        driverId:      form.driverId || undefined,
        registeredBy,
      }
      await registerMovement(payload)
      setForm(EMPTY_FORM)
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al registrar el movimiento. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Registrar movimiento"
      description="Ingresa los datos del ingreso o egreso"
      size="lg"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} loading={loading}>
            Registrar
          </Button>
        </>
      }
    >
      {error && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        <Select
          label="Tipo de movimiento"
          value={form.movementType}
          onChange={setField('movementType')}
          options={[
            { value: 'INCOME',  label: 'Ingreso' },
            { value: 'EXPENSE', label: 'Egreso' },
          ]}
          required
        />

        <Select
          label="Categoría"
          value={form.category}
          onChange={setField('category')}
          options={categoryOptions}
          required
        />

        <Input
          label="Monto (COP)"
          type="number"
          value={form.amount}
          onChange={setField('amount')}
          placeholder="Ej. 180000"
          min={1}
          required
        />

        <Select
          label="Método de pago"
          value={form.paymentMethod}
          onChange={setField('paymentMethod')}
          options={[
            { value: 'CASH',          label: 'Efectivo' },
            { value: 'BANK_TRANSFER', label: 'Transferencia bancaria' },
          ]}
          required
        />

        <Input
          label="Fecha del movimiento"
          type="date"
          value={form.date}
          onChange={setField('date')}
          max={today}
          required
        />

        <Select
          label="Vehículo asociado"
          value={form.vehicleId}
          onChange={setField('vehicleId')}
          options={vehicleOptions}
          required
        />

        <div className="col-span-2">
          <Input
            label="Descripción"
            value={form.description}
            onChange={setField('description')}
            placeholder="Breve descripción del movimiento"
          />
        </div>

        <div className="col-span-2">
          <Textarea
            label="Observaciones"
            value={form.notes}
            onChange={setField('notes')}
            placeholder="Notas adicionales (opcional)"
            rows={2}
          />
        </div>
      </div>
    </Modal>
  )
}

// ─── Movement Detail Modal ────────────────────────────────────────────────────

interface DetailModalProps {
  open: boolean
  onClose: () => void
  movement: FinancialMovement | null
  onCancelRequested: (m: FinancialMovement) => void
}

function MovementDetailModal({ open, onClose, movement, onCancelRequested }: DetailModalProps) {
  if (!movement) return null

  const isIncome = movement.movementType === 'INCOME'

  const rows: { label: string; value: React.ReactNode }[] = [
    { label: 'Tipo',           value: <MovementTypeBadge type={movement.movementType} /> },
    { label: 'Categoría',      value: CATEGORY_LABEL[movement.category] },
    { label: 'Monto',
      value: (
        <span className={`font-semibold tabular-nums ${isIncome ? 'text-ok' : 'text-err'}`}>
          {isIncome ? '+' : '−'}{formatCurrency(movement.amount)}
        </span>
      ),
    },
    { label: 'Fecha',          value: formatDate(movement.date) },
    { label: 'Método de pago', value: PAYMENT_METHOD_LABEL[movement.paymentMethod] },
    { label: 'Estado',         value: <MovementStatusBadge status={movement.status} /> },
    { label: 'Registrado por', value: movement.registeredBy },
    { label: 'Descripción',    value: movement.description || '—' },
    ...(movement.notes ? [{ label: 'Observaciones', value: movement.notes }] : []),
  ]

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Detalle del movimiento"
      size="md"
      footer={
        <>
          <Button variant="outline" onClick={onClose}>Cerrar</Button>
          {movement.status === 'ACTIVE' && (
            <Button
              variant="danger"
              icon={<Ban size={14} />}
              onClick={() => { onClose(); onCancelRequested(movement) }}
            >
              Cancelar movimiento
            </Button>
          )}
        </>
      }
    >
      <dl className="divide-y divide-slate-100">
        {rows.map(({ label, value }) => (
          <div key={label} className="grid grid-cols-2 gap-2 py-2.5 text-sm">
            <dt className="text-slate-500">{label}</dt>
            <dd className="text-slate-800">{value}</dd>
          </div>
        ))}
      </dl>
    </Modal>
  )
}

// ─── Fleet Balance Modal ──────────────────────────────────────────────────────

interface FleetBalanceModalProps {
  open: boolean
  onClose: () => void
}

function FleetBalanceModal({ open, onClose }: FleetBalanceModalProps) {
  const [dateFrom, setDateFrom] = useState(firstOfMonth)
  const [dateTo, setDateTo] = useState(today)
  const [queried, setQueried] = useState(false)
  const [balance, setBalance] = useState<FleetBalance | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleQuery = async () => {
    if (!dateFrom || !dateTo) return
    setError(null)
    setLoading(true)
    try {
      const data = await getFleetBalance(dateFrom, dateTo)
      setBalance(data)
      setQueried(true)
    } catch {
      setError('No se pudo cargar el balance. Verifica el rango de fechas.')
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    setQueried(false)
    setBalance(null)
    setError(null)
    onClose()
  }

  return (
    <Modal
      open={open}
      onClose={handleClose}
      title="Balance de la flota"
      description="Consulta el balance consolidado por período"
      size="md"
      footer={<Button variant="outline" onClick={handleClose}>Cerrar</Button>}
    >
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-3">
          <Input
            label="Desde"
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
            max={dateTo || today}
            required
          />
          <Input
            label="Hasta"
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.target.value)}
            min={dateFrom}
            max={today}
            required
          />
        </div>

        <Button
          onClick={handleQuery}
          loading={loading}
          disabled={!dateFrom || !dateTo}
          className="w-full"
        >
          Consultar balance
        </Button>

        {error && <Alert variant="error">{error}</Alert>}

        {queried && balance && (
          <div className="border border-slate-100 rounded-xl divide-y divide-slate-100 mt-2">
            <div className="grid grid-cols-2 gap-2 p-3 text-sm">
              <span className="text-slate-500">Vehículos en flota</span>
              <span className="text-slate-800 font-medium">{balance.totalVehicles}</span>
            </div>
            <div className="grid grid-cols-2 gap-2 p-3 text-sm">
              <span className="text-slate-500">Total ingresos</span>
              <span className="text-ok font-semibold tabular-nums">+{formatCurrency(balance.totalIncome)}</span>
            </div>
            <div className="grid grid-cols-2 gap-2 p-3 text-sm">
              <span className="text-slate-500">Total egresos</span>
              <span className="text-err font-semibold tabular-nums">−{formatCurrency(balance.totalExpense)}</span>
            </div>
            <div className="grid grid-cols-2 gap-2 p-3 text-sm bg-slate-50 rounded-b-xl">
              <span className="text-slate-700 font-semibold">Utilidad neta</span>
              <span
                className={`font-bold tabular-nums text-base ${
                  balance.netProfit >= 0 ? 'text-ok' : 'text-err'
                }`}
              >
                {balance.netProfit >= 0 ? '+' : '−'}{formatCurrency(Math.abs(balance.netProfit))}
              </span>
            </div>
          </div>
        )}
      </div>
    </Modal>
  )
}

// ─── Main Page ────────────────────────────────────────────────────────────────

export function FinancesPage() {
  const { user } = useAuth()
  const queryClient = useQueryClient()

  // ── Filtros ──
  const [typeFilter, setTypeFilter] = useState<MovementType | ''>('')
  const [categoryFilter, setCategoryFilter] = useState<MovementCategory | ''>('')
  const [paymentFilter, setPaymentFilter] = useState<PaymentMethod | ''>('')
  const [vehicleFilter, setVehicleFilter] = useState('')
  const [dateFromFilter, setDateFromFilter] = useState('')
  const [dateToFilter, setDateToFilter] = useState('')
  const [searchQuery, setSearchQuery] = useState('')

  // ── Modales ──
  const [registerOpen, setRegisterOpen] = useState(false)
  const [detailMovement, setDetailMovement] = useState<FinancialMovement | null>(null)
  const [cancelTarget, setCancelTarget] = useState<FinancialMovement | null>(null)
  const [balanceOpen, setBalanceOpen] = useState(false)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [cancelLoading, setCancelLoading] = useState(false)

  const apiFilters = useMemo<MovementsFilter>(() => ({
    movementType:  typeFilter    || undefined,
    category:      categoryFilter || undefined,
    paymentMethod: paymentFilter  || undefined,
    vehicleId:     vehicleFilter  || undefined,
    dateFrom:      dateFromFilter || undefined,
    dateTo:        dateToFilter   || undefined,
  }), [typeFilter, categoryFilter, paymentFilter, vehicleFilter, dateFromFilter, dateToFilter])

  const { data: movements = [], isLoading, error: fetchError } = useQuery({
    queryKey: ['finances', 'movements', apiFilters],
    queryFn: () => listMovements(apiFilters),
  })

  const { data: currentMonthBalance } = useQuery({
    queryKey: ['finances', 'balance', 'fleet', firstOfMonth, today],
    queryFn: () => getFleetBalance(firstOfMonth, today),
  })

  const { data: vehicles = [] } = useQuery({
    queryKey: ['vehicles'],
    queryFn: () => listVehicles(),
  })

  const vehicleFilterOptions = [
    { value: '', label: 'Todos los vehículos' },
    ...vehicles.map((v) => ({ value: v.id, label: v.plateNumber })),
  ]

  const categoryFilterOptions = useMemo(() => {
    const base = [{ value: '', label: 'Todas las categorías' }]
    const cats = typeFilter === 'INCOME'
      ? INCOME_CATEGORIES
      : typeFilter === 'EXPENSE'
      ? EXPENSE_CATEGORIES
      : [...INCOME_CATEGORIES, ...EXPENSE_CATEGORIES]
    return [...base, ...cats]
  }, [typeFilter])

  const filtered = useMemo(() => {
    if (!searchQuery) return movements
    const q = searchQuery.toLowerCase()
    return movements.filter(
      (m) =>
        m.description?.toLowerCase().includes(q) ||
        m.registeredBy?.toLowerCase().includes(q) ||
        CATEGORY_LABEL[m.category]?.toLowerCase().includes(q),
    )
  }, [movements, searchQuery])

  const invalidate = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['finances'] })
  }, [queryClient])

  const showSuccess = useCallback((msg: string) => {
    setSuccessMessage(msg)
    invalidate()
  }, [invalidate])

  const handleConfirmCancel = async () => {
    if (!cancelTarget) return
    setCancelLoading(true)
    try {
      await cancelMovement(cancelTarget.id)
      setCancelTarget(null)
      showSuccess('Movimiento cancelado correctamente')
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      const msg = Array.isArray(data) ? data[0] : typeof data === 'string' ? data : 'Error al cancelar el movimiento.'
      setErrorMessage(msg)
    } finally {
      setCancelLoading(false)
    }
  }

  const clearFilters = () => {
    setTypeFilter('')
    setCategoryFilter('')
    setPaymentFilter('')
    setVehicleFilter('')
    setDateFromFilter('')
    setDateToFilter('')
    setSearchQuery('')
  }

  const hasFilters = typeFilter || categoryFilter || paymentFilter || vehicleFilter || dateFromFilter || dateToFilter || searchQuery

  return (
    <Page
      title="Finanzas"
      subtitle="Movimientos financieros de la flota"
      actions={
        <div className="flex gap-2">
          <Button
            variant="outline"
            icon={<BarChart2 size={14} />}
            onClick={() => setBalanceOpen(true)}
          >
            Balance de flota
          </Button>
          <Button
            icon={<Plus size={14} />}
            onClick={() => setRegisterOpen(true)}
          >
            Registrar movimiento
          </Button>
        </div>
      }
    >
      {successMessage && (
        <div className="mb-4">
          <Alert variant="success" onClose={() => setSuccessMessage(null)}>
            {successMessage}
          </Alert>
        </div>
      )}
      {errorMessage && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setErrorMessage(null)}>
            {errorMessage}
          </Alert>
        </div>
      )}

      {/* Stats del mes actual (balance servidor) */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <StatCard
          label="Ingresos del mes"
          value={formatCurrency(currentMonthBalance?.totalIncome ?? 0)}
          variant="success"
          icon={<TrendingUp size={18} />}
        />
        <StatCard
          label="Egresos del mes"
          value={formatCurrency(currentMonthBalance?.totalExpense ?? 0)}
          variant="danger"
          icon={<TrendingDown size={18} />}
        />
        <StatCard
          label="Balance neto"
          value={formatCurrency(currentMonthBalance?.netProfit ?? 0)}
          variant={(currentMonthBalance?.netProfit ?? 0) >= 0 ? 'default' : 'danger'}
          icon={<DollarSign size={18} />}
        />
      </div>

      <Card noPadding>
        {/* Filtros */}
        <div className="p-4 border-b border-blue-50 space-y-3">
          <div className="flex flex-wrap items-center gap-2">
            <Input
              placeholder="Buscar descripción, categoría, usuario…"
              leadingIcon={<Search size={13} />}
              className="max-w-xs h-8 text-xs"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <Select
              options={MOVEMENT_TYPE_FILTER_OPTIONS}
              value={typeFilter}
              onChange={(e) => {
                setTypeFilter(e.target.value as MovementType | '')
                setCategoryFilter('')
              }}
              className="w-40 h-8 text-xs"
            />
            <Select
              options={categoryFilterOptions}
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value as MovementCategory | '')}
              className="w-48 h-8 text-xs"
            />
            <Select
              options={PAYMENT_METHOD_FILTER_OPTIONS}
              value={paymentFilter}
              onChange={(e) => setPaymentFilter(e.target.value as PaymentMethod | '')}
              className="w-44 h-8 text-xs"
            />
            <Select
              options={vehicleFilterOptions}
              value={vehicleFilter}
              onChange={(e) => setVehicleFilter(e.target.value)}
              className="w-36 h-8 text-xs"
            />
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <Input
              type="date"
              value={dateFromFilter}
              onChange={(e) => setDateFromFilter(e.target.value)}
              className="h-8 text-xs w-36"
              max={dateToFilter || today}
            />
            <span className="text-slate-400 text-xs">—</span>
            <Input
              type="date"
              value={dateToFilter}
              onChange={(e) => setDateToFilter(e.target.value)}
              className="h-8 text-xs w-36"
              min={dateFromFilter}
              max={today}
            />
            {hasFilters && (
              <button
                onClick={clearFilters}
                className="text-xs text-blue-500 hover:text-blue-700 underline ml-1"
              >
                Limpiar filtros
              </button>
            )}
            <Badge variant="default" className="ml-auto">
              {filtered.length} movimiento{filtered.length !== 1 ? 's' : ''}
            </Badge>
          </div>
        </div>

        {/* Tabla */}
        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Fecha</Table.Th>
              <Table.Th>Descripción</Table.Th>
              <Table.Th>Categoría</Table.Th>
              <Table.Th>Tipo</Table.Th>
              <Table.Th>Método</Table.Th>
              <Table.Th>Monto</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {isLoading ? (
              <Table.Empty
                icon={<RefreshCw size={28} className="animate-spin" />}
                title="Cargando movimientos…"
              />
            ) : fetchError ? (
              <Table.Empty
                icon={<DollarSign size={36} />}
                title="Error al cargar movimientos"
                description="Verifica tu conexión e intenta de nuevo"
              />
            ) : filtered.length === 0 ? (
              <Table.Empty
                icon={<DollarSign size={36} />}
                title="Sin movimientos"
                description={
                  hasFilters
                    ? 'No hay movimientos con los filtros aplicados'
                    : 'Registra el primer movimiento financiero'
                }
                action={
                  !hasFilters ? (
                    <Button
                      size="sm"
                      icon={<Plus size={13} />}
                      onClick={() => setRegisterOpen(true)}
                    >
                      Registrar movimiento
                    </Button>
                  ) : undefined
                }
              />
            ) : (
              filtered.map((m) => (
                <Table.Row key={m.id}>
                  <Table.Td className="text-slate-500 text-xs tabular-nums">
                    {formatDate(m.date)}
                  </Table.Td>
                  <Table.Td>
                    <p className="font-medium text-slate-800 max-w-xs truncate">
                      {m.description || '—'}
                    </p>
                    <p className="text-xs text-slate-400 mt-0.5">{m.registeredBy}</p>
                  </Table.Td>
                  <Table.Td className="text-slate-500 text-xs">
                    {CATEGORY_LABEL[m.category]}
                  </Table.Td>
                  <Table.Td>
                    <MovementTypeBadge type={m.movementType} />
                  </Table.Td>
                  <Table.Td className="text-slate-500 text-xs">
                    {PAYMENT_METHOD_LABEL[m.paymentMethod]}
                  </Table.Td>
                  <Table.Td>
                    <span
                      className={`font-semibold tabular-nums text-sm ${
                        m.movementType === 'INCOME' ? 'text-ok' : 'text-err'
                      }`}
                    >
                      {m.movementType === 'EXPENSE' ? '−' : '+'}{formatCurrency(m.amount)}
                    </span>
                  </Table.Td>
                  <Table.Td>
                    <MovementStatusBadge status={m.status} />
                  </Table.Td>
                  <Table.Td>
                    <div className="flex items-center gap-1 justify-end">
                      <button
                        title="Ver detalle"
                        onClick={() => setDetailMovement(m)}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                      >
                        <MoreHorizontal size={14} />
                      </button>
                      {m.status === 'ACTIVE' && (
                        <button
                          title="Cancelar movimiento"
                          onClick={() => setCancelTarget(m)}
                          className="p-1.5 rounded-lg text-slate-300 hover:text-red-500 hover:bg-red-50 transition-all"
                        >
                          <Ban size={14} />
                        </button>
                      )}
                    </div>
                  </Table.Td>
                </Table.Row>
              ))
            )}
          </Table.Body>
        </Table>
      </Card>

      {/* Modales */}
      <RegisterMovementModal
        key={registerOpen ? 'open' : 'closed'}
        open={registerOpen}
        onClose={() => setRegisterOpen(false)}
        onSuccess={() => showSuccess('Movimiento registrado correctamente')}
        vehicles={vehicles}
        registeredBy={user?.username ?? 'unknown'}
      />

      <MovementDetailModal
        open={!!detailMovement}
        onClose={() => setDetailMovement(null)}
        movement={detailMovement}
        onCancelRequested={(m) => setCancelTarget(m)}
      />

      <ConfirmModal
        open={!!cancelTarget}
        onClose={() => setCancelTarget(null)}
        title="Cancelar movimiento"
        message={
          cancelTarget
            ? `¿Confirmas que deseas cancelar el movimiento de ${formatCurrency(cancelTarget.amount)} (${
                CATEGORY_LABEL[cancelTarget.category]
              })? Esta acción no se puede deshacer.`
            : ''
        }
        confirmLabel="Cancelar movimiento"
        confirmVariant="danger"
        loading={cancelLoading}
        onConfirm={handleConfirmCancel}
      />

      <FleetBalanceModal
        open={balanceOpen}
        onClose={() => setBalanceOpen(false)}
      />
    </Page>
  )
}
