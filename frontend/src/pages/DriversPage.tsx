import { useState, useCallback } from 'react'
import { Search, Plus, UserCircle, RefreshCw, MoreHorizontal, UserCheck, UserX, AlertTriangle } from 'lucide-react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Page } from '@/components/layout'
import {
  Button, Card, Table, DriverStatusBadge, Input, Select, Badge,
  Modal, ConfirmModal, Alert,
} from '@/components/ui'
import { formatDate, daysUntil } from '@/lib/utils'
import type { Driver, DriverStatus, LicenseCategory, IdentificationType } from '@/types/domain'
import {
  listDrivers, createDriver, updateDriver, changeDriverStatus,
  type CreateDriverPayload, type UpdateDriverPayload,
} from '@/services/drivers'

// ─── Constants ────────────────────────────────────────────────────────────────

const STATUS_OPTIONS = [
  { value: '', label: 'Todos los estados' },
  { value: 'ACTIVE', label: 'Activo' },
  { value: 'INACTIVE', label: 'Inactivo' },
]

const CATEGORY_OPTIONS = [
  { value: '', label: 'Todas las categorías' },
  { value: 'A1', label: 'A1' },
  { value: 'A2', label: 'A2' },
  { value: 'B1', label: 'B1' },
  { value: 'B2', label: 'B2' },
  { value: 'B3', label: 'B3' },
  { value: 'C1', label: 'C1' },
  { value: 'C2', label: 'C2' },
  { value: 'C3', label: 'C3' },
]

const CATEGORY_FORM_OPTIONS = CATEGORY_OPTIONS.slice(1)

const ID_TYPE_OPTIONS = [
  { value: 'CC', label: 'Cédula de ciudadanía (CC)' },
  { value: 'CE', label: 'Cédula de extranjería (CE)' },
  { value: 'PA', label: 'Pasaporte (PA)' },
]

// ─── License expiry indicator ──────────────────────────────────────────────────

function LicenseExpiryCell({ dateStr }: { dateStr: string }) {
  const days = daysUntil(dateStr)
  const expired = days !== null && days < 0
  const expiringSoon = days !== null && days >= 0 && days <= 30

  return (
    <div className="flex items-center gap-1.5">
      <span className={expired || expiringSoon ? 'text-warn font-medium' : 'text-slate-500'}>
        {formatDate(dateStr)}
      </span>
      {expired && (
        <span title="Licencia vencida">
          <AlertTriangle size={13} className="text-red-500" />
        </span>
      )}
      {expiringSoon && !expired && (
        <span title={`Vence en ${days} día${days !== 1 ? 's' : ''}`}>
          <AlertTriangle size={13} className="text-warn" />
        </span>
      )}
    </div>
  )
}

// ─── Driver form modal ────────────────────────────────────────────────────────

interface DriverFormValues {
  identificationType: IdentificationType
  identificationNumber: string
  firstName: string
  lastName: string
  licenseNumber: string
  licenseCategory: LicenseCategory
  licenseExpiry: string
  phone: string
  email: string
  address: string
  registrationDate: string
}

const EMPTY_FORM: DriverFormValues = {
  identificationType: 'CC',
  identificationNumber: '',
  firstName: '',
  lastName: '',
  licenseNumber: '',
  licenseCategory: 'B3',
  licenseExpiry: '',
  phone: '',
  email: '',
  address: '',
  registrationDate: '',
}

interface DriverFormModalProps {
  open: boolean
  onClose: () => void
  editing: Driver | null
  onSuccess: () => void
}

function DriverFormModal({ open, onClose, editing, onSuccess }: DriverFormModalProps) {
  const [form, setForm] = useState<DriverFormValues>(
    editing
      ? {
          identificationType: editing.identificationType,
          identificationNumber: editing.identificationNumber,
          firstName: editing.firstName,
          lastName: editing.lastName,
          licenseNumber: editing.licenseNumber,
          licenseCategory: editing.licenseCategory,
          licenseExpiry: editing.licenseExpiry,
          phone: editing.phone ?? '',
          email: editing.email ?? '',
          address: editing.address ?? '',
          registrationDate: editing.registrationDate ?? '',
        }
      : EMPTY_FORM,
  )
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const setField = (field: keyof DriverFormValues) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async () => {
    setError(null)
    setLoading(true)
    try {
      if (editing) {
        const payload: UpdateDriverPayload = {
          firstName: form.firstName,
          lastName: form.lastName,
          licenseCategory: form.licenseCategory as LicenseCategory,
          licenseExpiry: form.licenseExpiry,
          phone: form.phone || undefined,
          email: form.email || undefined,
          address: form.address || undefined,
        }
        await updateDriver(editing.id, payload)
      } else {
        const payload: CreateDriverPayload = {
          identificationType: form.identificationType as IdentificationType,
          identificationNumber: form.identificationNumber,
          firstName: form.firstName,
          lastName: form.lastName,
          licenseNumber: form.licenseNumber,
          licenseCategory: form.licenseCategory as LicenseCategory,
          licenseExpiry: form.licenseExpiry,
          phone: form.phone || undefined,
          email: form.email || undefined,
          address: form.address || undefined,
          registrationDate: form.registrationDate || undefined,
        }
        await createDriver(payload)
      }
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al guardar. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={editing ? 'Editar conductor' : 'Nuevo conductor'}
      description={
        editing
          ? `Editando a ${editing.firstName} ${editing.lastName}`
          : 'Completa los datos del conductor'
      }
      size="lg"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
          <Button onClick={handleSubmit} loading={loading}>
            {editing ? 'Guardar cambios' : 'Registrar conductor'}
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
        <Input
          label="Nombres"
          value={form.firstName}
          onChange={setField('firstName')}
          required
          placeholder="Ej. Carlos Andrés"
        />
        <Input
          label="Apellidos"
          value={form.lastName}
          onChange={setField('lastName')}
          required
          placeholder="Ej. Rodríguez Gómez"
        />

        {!editing && (
          <>
            <Select
              label="Tipo de identificación"
              value={form.identificationType}
              onChange={setField('identificationType')}
              options={ID_TYPE_OPTIONS}
              required
            />
            <Input
              label="Número de identificación"
              value={form.identificationNumber}
              onChange={setField('identificationNumber')}
              required
              placeholder="6–12 dígitos"
            />
            <Input
              label="Número de licencia"
              value={form.licenseNumber}
              onChange={setField('licenseNumber')}
              required
              placeholder="Ej. B3-001234"
            />
          </>
        )}

        <Select
          label="Categoría de licencia"
          value={form.licenseCategory}
          onChange={setField('licenseCategory')}
          options={CATEGORY_FORM_OPTIONS}
          required
        />
        <Input
          label="Vencimiento de licencia"
          type="date"
          value={form.licenseExpiry}
          onChange={setField('licenseExpiry')}
          required
        />

        <Input
          label="Teléfono"
          type="tel"
          value={form.phone}
          onChange={setField('phone')}
          placeholder="7–15 dígitos"
        />
        <Input
          label="Correo electrónico"
          type="email"
          value={form.email}
          onChange={setField('email')}
          placeholder="correo@ejemplo.com"
          disabled={!!editing}
        />

        <div className="col-span-2">
          <Input
            label="Dirección de residencia"
            value={form.address}
            onChange={setField('address')}
            placeholder="Calle, carrera, barrio…"
          />
        </div>

        {!editing && (
          <Input
            label="Fecha de registro"
            type="date"
            value={form.registrationDate}
            onChange={setField('registrationDate')}
          />
        )}
      </div>
    </Modal>
  )
}

// ─── Main page ────────────────────────────────────────────────────────────────

export function DriversPage() {
  const queryClient = useQueryClient()

  const [statusFilter, setStatusFilter] = useState<DriverStatus | ''>('')
  const [categoryFilter, setCategoryFilter] = useState<LicenseCategory | ''>('')
  const [searchQuery, setSearchQuery] = useState('')
  const [driverModal, setDriverModal] = useState<{ open: boolean; editing: Driver | null }>({
    open: false,
    editing: null,
  })
  const [statusConfirm, setStatusConfirm] = useState<{
    open: boolean; driver: Driver | null
  }>({ open: false, driver: null })
  const [pendingId, setPendingId] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const invalidate = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['drivers'] })
  }, [queryClient])

  const { data: drivers = [], isLoading, error: fetchError } = useQuery({
    queryKey: ['drivers', statusFilter, categoryFilter],
    queryFn: () =>
      listDrivers({
        status: statusFilter || undefined,
        licenseCategory: categoryFilter || undefined,
      }),
  })

  const filteredDrivers = drivers.filter((d) => {
    if (!searchQuery) return true
    const q = searchQuery.toLowerCase()
    return (
      d.firstName.toLowerCase().includes(q) ||
      d.lastName.toLowerCase().includes(q) ||
      d.identificationNumber.includes(q) ||
      d.licenseNumber.toLowerCase().includes(q)
    )
  })

  const expiringCount = drivers.filter((d) => {
    const days = daysUntil(d.licenseExpiry)
    return days !== null && days <= 30
  }).length

  const handleChangeStatus = async () => {
    if (!statusConfirm.driver) return
    const { driver } = statusConfirm
    const newStatus: DriverStatus = driver.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    setPendingId(driver.id)
    try {
      await changeDriverStatus(driver.id, newStatus)
      setSuccessMessage(
        newStatus === 'ACTIVE' ? 'Conductor activado correctamente' : 'Conductor desactivado correctamente',
      )
      invalidate()
    } catch {
      setSuccessMessage(null)
    } finally {
      setPendingId(null)
      setStatusConfirm({ open: false, driver: null })
    }
  }

  return (
    <Page
      title="Conductores"
      subtitle="Gestión de conductores de la flota"
      actions={
        <Button
          icon={<Plus size={14} />}
          onClick={() => setDriverModal({ open: true, editing: null })}
        >
          Nuevo conductor
        </Button>
      }
    >
      {successMessage && (
        <div className="mb-4">
          <Alert variant="success" onClose={() => setSuccessMessage(null)}>{successMessage}</Alert>
        </div>
      )}

      {expiringCount > 0 && (
        <div className="mb-4">
          <Alert variant="warning">
            <span className="flex items-center gap-1.5">
              <AlertTriangle size={14} />
              {expiringCount} licencia{expiringCount !== 1 ? 's' : ''} próxima{expiringCount !== 1 ? 's' : ''} a vencer o ya vencida{expiringCount !== 1 ? 's' : ''}
            </span>
          </Alert>
        </div>
      )}

      <Card noPadding>
        {/* Filters */}
        <div className="flex flex-wrap items-center gap-3 p-4 border-b border-blue-50">
          <Input
            placeholder="Buscar por nombre, identificación o licencia…"
            leadingIcon={<Search size={13} />}
            className="max-w-xs h-8 text-xs"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Select
            options={STATUS_OPTIONS}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as DriverStatus | '')}
            className="w-44 h-8 text-xs"
          />
          <Select
            options={CATEGORY_OPTIONS}
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value as LicenseCategory | '')}
            className="w-44 h-8 text-xs"
          />
          <Badge variant="default" className="ml-auto">
            {filteredDrivers.length} conductor{filteredDrivers.length !== 1 ? 'es' : ''}
          </Badge>
        </div>

        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Conductor</Table.Th>
              <Table.Th>Identificación</Table.Th>
              <Table.Th>Licencia</Table.Th>
              <Table.Th>Categoría</Table.Th>
              <Table.Th>Venc. licencia</Table.Th>
              <Table.Th>Teléfono</Table.Th>
              <Table.Th>Registro</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {isLoading ? (
              <Table.Empty
                icon={<RefreshCw size={28} className="animate-spin" />}
                title="Cargando conductores…"
              />
            ) : fetchError ? (
              <Table.Empty
                icon={<UserCircle size={36} />}
                title="Error al cargar conductores"
                description="Verifica tu conexión e intenta de nuevo"
              />
            ) : filteredDrivers.length === 0 ? (
              <Table.Empty
                icon={<UserCircle size={36} />}
                title="Sin conductores registrados"
                description={
                  searchQuery || statusFilter || categoryFilter
                    ? 'No hay conductores con los filtros aplicados'
                    : 'Agrega el primer conductor para comenzar'
                }
                action={
                  !searchQuery && !statusFilter && !categoryFilter ? (
                    <Button
                      size="sm"
                      icon={<Plus size={13} />}
                      onClick={() => setDriverModal({ open: true, editing: null })}
                    >
                      Nuevo conductor
                    </Button>
                  ) : undefined
                }
              />
            ) : (
              filteredDrivers.map((d) => (
                <Table.Row key={d.id}>
                  <Table.Td>
                    <p className="font-medium text-slate-800">{d.firstName} {d.lastName}</p>
                    {d.email && <p className="text-xs text-slate-400 mt-0.5">{d.email}</p>}
                  </Table.Td>
                  <Table.Td>
                    <span className="font-mono text-xs text-slate-500">
                      {d.identificationType} {d.identificationNumber}
                    </span>
                  </Table.Td>
                  <Table.Td>
                    <span className="font-mono text-xs text-slate-500">{d.licenseNumber}</span>
                  </Table.Td>
                  <Table.Td>
                    <Badge variant="default">{d.licenseCategory}</Badge>
                  </Table.Td>
                  <Table.Td>
                    <LicenseExpiryCell dateStr={d.licenseExpiry} />
                  </Table.Td>
                  <Table.Td className="text-slate-500">{d.phone || '—'}</Table.Td>
                  <Table.Td className="text-slate-400 text-xs">
                    {formatDate(d.registrationDate)}
                  </Table.Td>
                  <Table.Td>
                    <DriverStatusBadge status={d.status} />
                  </Table.Td>
                  <Table.Td>
                    <div className="flex items-center gap-1 justify-end">
                      <button
                        title="Editar"
                        onClick={() => setDriverModal({ open: true, editing: d })}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                      >
                        <MoreHorizontal size={14} />
                      </button>
                      <button
                        title={d.status === 'ACTIVE' ? 'Desactivar' : 'Activar'}
                        onClick={() => setStatusConfirm({ open: true, driver: d })}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                      >
                        {d.status === 'ACTIVE' ? <UserX size={14} /> : <UserCheck size={14} />}
                      </button>
                    </div>
                  </Table.Td>
                </Table.Row>
              ))
            )}
          </Table.Body>
        </Table>
      </Card>

      {/* Modals */}
      <DriverFormModal
        key={driverModal.editing?.id ?? 'new'}
        open={driverModal.open}
        onClose={() => setDriverModal({ open: false, editing: null })}
        editing={driverModal.editing}
        onSuccess={() => {
          setSuccessMessage(
            driverModal.editing ? 'Conductor actualizado correctamente' : 'Conductor registrado correctamente',
          )
          invalidate()
        }}
      />

      <ConfirmModal
        open={statusConfirm.open}
        onClose={() => setStatusConfirm({ open: false, driver: null })}
        onConfirm={handleChangeStatus}
        title={
          statusConfirm.driver?.status === 'ACTIVE'
            ? 'Desactivar conductor'
            : 'Activar conductor'
        }
        message={
          statusConfirm.driver?.status === 'ACTIVE'
            ? `¿Desactivar a ${statusConfirm.driver?.firstName} ${statusConfirm.driver?.lastName}? Se desasignará del vehículo actual si tiene uno.`
            : `¿Activar a ${statusConfirm.driver?.firstName} ${statusConfirm.driver?.lastName}?`
        }
        confirmLabel={statusConfirm.driver?.status === 'ACTIVE' ? 'Desactivar' : 'Activar'}
        confirmVariant={statusConfirm.driver?.status === 'ACTIVE' ? 'danger' : 'primary'}
        loading={pendingId === statusConfirm.driver?.id}
      />
    </Page>
  )
}
