import { useState, useCallback } from 'react'
import { Search, Plus, Truck, RefreshCw, MoreHorizontal, UserPlus, UserMinus } from 'lucide-react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Page } from '@/components/layout'
import {
  Button, Card, Table, VehicleStatusBadge, Input, Select, Textarea, Badge,
  Modal, Alert,
} from '@/components/ui'
import { formatDate } from '@/lib/utils'
import type { Vehicle, VehicleStatus, VehicleType } from '@/types/domain'
import {
  listVehicles, createVehicle, updateVehicle, changeVehicleStatus,
  assignDriver, unassignDriver,
  type CreateVehiclePayload, type UpdateVehiclePayload,
} from '@/services/vehicles'
import { listDrivers } from '@/services/drivers'

// ─── Constants ────────────────────────────────────────────────────────────────

const STATUS_OPTIONS = [
  { value: '', label: 'Todos los estados' },
  { value: 'ACTIVE', label: 'Activo' },
  { value: 'UNDER_MAINTENANCE', label: 'En mantenimiento' },
  { value: 'INACTIVE', label: 'Inactivo' },
]

const STATUS_CHANGE_OPTIONS = [
  { value: 'ACTIVE', label: 'Activo' },
  { value: 'UNDER_MAINTENANCE', label: 'En mantenimiento' },
  { value: 'INACTIVE', label: 'Inactivo' },
]

const TYPE_OPTIONS = [
    { value: '', label: 'Todos los tipos' },
    { value: 'TAXI', label: 'Taxi' },
    { value: 'BUS', label: 'Bus' },
    { value: 'BUSETA', label: 'Buseta' },
    { value: 'MICROBUS', label: 'Microbús' },
    { value: 'VAN', label: 'Van' },
]

const TYPE_FORM_OPTIONS = TYPE_OPTIONS.slice(1)

const TYPE_LABEL: Record<VehicleType, string> = {
    TAXI: 'Taxi',
    BUS: 'Bus',
    BUSETA: 'Buseta',
    MICROBUS: 'Microbús',
    VAN: 'Van',
}

const CURRENT_YEAR = new Date().getFullYear()

// ─── Vehicle form modal ───────────────────────────────────────────────────────

interface VehicleFormValues {
  plateNumber: string
  brand: string
  vehicleModel: string
  vehicleType: VehicleType
  year: string
  status: VehicleStatus
  responsibleName: string
  registrationDate: string
  notes: string
}

const EMPTY_FORM: VehicleFormValues = {
  plateNumber: '',
  brand: '',
  vehicleModel: '',
  vehicleType: 'BUS',
  year: '',
  status: 'ACTIVE',
  responsibleName: '',
  registrationDate: '',
  notes: '',
}

interface VehicleFormModalProps {
  open: boolean
  onClose: () => void
  editing: Vehicle | null
  onSuccess: () => void
}

function VehicleFormModal({ open, onClose, editing, onSuccess }: VehicleFormModalProps) {
  const [form, setForm] = useState<VehicleFormValues>(
    editing
      ? {
          plateNumber: editing.plateNumber,
          brand: editing.brand,
          vehicleModel: editing.vehicleModel,
          vehicleType: editing.vehicleType,
          year: editing.year ? String(editing.year) : '',
          status: editing.status,
          responsibleName: editing.responsibleName ?? '',
          registrationDate: editing.registrationDate ?? '',
          notes: editing.notes ?? '',
        }
      : EMPTY_FORM,
  )
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const setField =
    (field: keyof VehicleFormValues) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async () => {
    setError(null)
    setLoading(true)
    try {
      if (editing) {
        const payload: UpdateVehiclePayload = {
          brand: form.brand,
          vehicleModel: form.vehicleModel,
          vehicleType: form.vehicleType,
          year: form.year ? Number(form.year) : undefined,
          responsibleName: form.responsibleName || undefined,
          notes: form.notes || undefined,
        }
        await updateVehicle(editing.id, payload)
      } else {
        const payload: CreateVehiclePayload = {
          plateNumber: form.plateNumber,
          brand: form.brand,
          vehicleModel: form.vehicleModel,
          vehicleType: form.vehicleType,
          year: form.year ? Number(form.year) : undefined,
          status: form.status,
          responsibleName: form.responsibleName || undefined,
          registrationDate: form.registrationDate || undefined,
          notes: form.notes || undefined,
        }
        await createVehicle(payload)
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
      title={editing ? 'Editar vehículo' : 'Nuevo vehículo'}
      description={editing ? `Editando ${editing.plateNumber}` : 'Completa los datos del vehículo'}
      size="lg"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} loading={loading}>
            {editing ? 'Guardar cambios' : 'Registrar vehículo'}
          </Button>
        </>
      }
    >
      {error && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setError(null)}>
            {error}
          </Alert>
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        {!editing && (
          <Input
            label="Placa"
            value={form.plateNumber}
            onChange={setField('plateNumber')}
            required
            placeholder="Ej. ABC-123 o ABC-12D"
          />
        )}

        <Input
          label="Marca"
          value={form.brand}
          onChange={setField('brand')}
          required
          placeholder="Ej. Chevrolet"
        />
        <Input
          label="Modelo"
          value={form.vehicleModel}
          onChange={setField('vehicleModel')}
          required
          placeholder="Ej. NPR"
        />

        <Select
          label="Tipo de vehículo"
          value={form.vehicleType}
          onChange={setField('vehicleType')}
          options={TYPE_FORM_OPTIONS}
          required
        />
        <Input
          label="Año"
          type="number"
          value={form.year}
          onChange={setField('year')}
          placeholder={`1990–${CURRENT_YEAR + 1}`}
          min={1990}
          max={CURRENT_YEAR + 1}
        />

        {!editing && (
          <Select
            label="Estado inicial"
            value={form.status}
            onChange={setField('status')}
            options={STATUS_CHANGE_OPTIONS}
            required
          />
        )}

        <Input
          label="Responsable"
          value={form.responsibleName}
          onChange={setField('responsibleName')}
          placeholder="Nombre del propietario o responsable"
        />

        {!editing && (
          <Input
            label="Fecha de registro"
            type="date"
            value={form.registrationDate}
            onChange={setField('registrationDate')}
            max={new Date().toISOString().split('T')[0]}
          />
        )}

        <div className="col-span-2">
          <Textarea
            label="Observaciones"
            value={form.notes}
            onChange={setField('notes')}
            placeholder="Notas adicionales sobre el vehículo…"
            rows={2}
          />
        </div>
      </div>
    </Modal>
  )
}

// ─── Change status modal ──────────────────────────────────────────────────────

interface ChangeStatusModalProps {
  open: boolean
  onClose: () => void
  vehicle: Vehicle | null
  onSuccess: () => void
}

function ChangeStatusModal({ open, onClose, vehicle, onSuccess }: ChangeStatusModalProps) {
  const otherStatuses = STATUS_CHANGE_OPTIONS.filter((o) => o.value !== vehicle?.status)
  const [newStatus, setNewStatus] = useState<VehicleStatus>(
    (otherStatuses[0]?.value as VehicleStatus) ?? 'ACTIVE',
  )
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleConfirm = async () => {
    if (!vehicle) return
    setError(null)
    setLoading(true)
    try {
      await changeVehicleStatus(vehicle.id, newStatus)
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al cambiar el estado.')
    } finally {
      setLoading(false)
    }
  }

  const driverWillBeUnassigned =
    (newStatus === 'UNDER_MAINTENANCE' || newStatus === 'INACTIVE') && !!vehicle?.assignedDriverId

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Cambiar estado del vehículo"
      description={vehicle ? `Vehículo ${vehicle.plateNumber}` : ''}
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button
            variant={newStatus === 'ACTIVE' ? 'primary' : 'danger'}
            onClick={handleConfirm}
            loading={loading}
          >
            Confirmar cambio
          </Button>
        </>
      }
    >
      {error && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setError(null)}>
            {error}
          </Alert>
        </div>
      )}

      <div className="space-y-4">
        <Select
          label="Nuevo estado"
          value={newStatus}
          onChange={(e) => setNewStatus(e.target.value as VehicleStatus)}
          options={otherStatuses}
          required
        />
        {driverWillBeUnassigned && (
          <Alert variant="warning">
            El conductor asignado será desvinculado automáticamente al cambiar a este estado.
          </Alert>
        )}
      </div>
    </Modal>
  )
}

// ─── Assign driver modal ──────────────────────────────────────────────────────

interface AssignDriverModalProps {
  open: boolean
  onClose: () => void
  vehicle: Vehicle | null
  onSuccess: () => void
}

function AssignDriverModal({ open, onClose, vehicle, onSuccess }: AssignDriverModalProps) {
  const [driverId, setDriverId] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const { data: drivers = [] } = useQuery({
    queryKey: ['drivers', 'ACTIVE'],
    queryFn: () => listDrivers({ status: 'ACTIVE' }),
    enabled: open && !vehicle?.assignedDriverId,
  })

  const driverOptions = [
    { value: '', label: 'Selecciona un conductor…' },
    ...drivers.map((d) => ({
      value: d.id,
      label: `${d.firstName} ${d.lastName} — ${d.identificationNumber}`,
    })),
  ]

  const isUnassigning = !!vehicle?.assignedDriverId

  const handleConfirm = async () => {
    if (!vehicle) return
    setError(null)
    setLoading(true)
    try {
      if (isUnassigning) {
        await unassignDriver(vehicle.id)
      } else {
        await assignDriver(vehicle.id, driverId)
      }
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al procesar la asignación.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={isUnassigning ? 'Desasignar conductor' : 'Asignar conductor'}
      description={vehicle ? `Vehículo ${vehicle.plateNumber}` : ''}
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
          <Button
            variant={isUnassigning ? 'danger' : 'primary'}
            onClick={handleConfirm}
            loading={loading}
            disabled={!isUnassigning && !driverId}
          >
            {isUnassigning ? 'Desasignar conductor' : 'Asignar conductor'}
          </Button>
        </>
      }
    >
      {error && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setError(null)}>
            {error}
          </Alert>
        </div>
      )}

      {isUnassigning ? (
        <p className="text-sm text-slate-600">
          ¿Desasignar al conductor actualmente asignado del vehículo{' '}
          <strong>{vehicle?.plateNumber}</strong>?
        </p>
      ) : (
        <Select
          label="Conductor activo"
          value={driverId}
          onChange={(e) => setDriverId(e.target.value)}
          options={driverOptions}
          required
        />
      )}
    </Modal>
  )
}

// ─── Main page ────────────────────────────────────────────────────────────────

export function VehiclesPage() {
  const queryClient = useQueryClient()

  const [statusFilter, setStatusFilter] = useState<VehicleStatus | ''>('')
  const [typeFilter, setTypeFilter] = useState<VehicleType | ''>('')
  const [searchQuery, setSearchQuery] = useState('')
  const [vehicleModal, setVehicleModal] = useState<{ open: boolean; editing: Vehicle | null }>({
    open: false,
    editing: null,
  })
  const [statusModal, setStatusModal] = useState<{ open: boolean; vehicle: Vehicle | null }>({
    open: false,
    vehicle: null,
  })
  const [assignModal, setAssignModal] = useState<{ open: boolean; vehicle: Vehicle | null }>({
    open: false,
    vehicle: null,
  })
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const invalidate = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['vehicles'] })
  }, [queryClient])

  const showSuccess = useCallback(
    (msg: string) => {
      setSuccessMessage(msg)
      invalidate()
    },
    [invalidate],
  )

  const { data: vehicles = [], isLoading, error: fetchError } = useQuery({
    queryKey: ['vehicles', statusFilter],
    queryFn: () => listVehicles({ status: statusFilter || undefined }),
  })

  const filteredVehicles = vehicles.filter((v) => {
    if (typeFilter && v.vehicleType !== typeFilter) return false
    if (!searchQuery) return true
    const q = searchQuery.toLowerCase()
    return (
      v.plateNumber.toLowerCase().includes(q) ||
      v.brand.toLowerCase().includes(q) ||
      v.vehicleModel.toLowerCase().includes(q)
    )
  })

  return (
    <Page
      title="Vehículos"
      subtitle="Inventario y estado de la flota"
      actions={
        <Button
          icon={<Plus size={14} />}
          onClick={() => setVehicleModal({ open: true, editing: null })}
        >
          Nuevo vehículo
        </Button>
      }
    >
      {successMessage && (
        <div className="mb-4">
          <Alert variant="success" onClose={() => setSuccessMessage(null)}>
            {successMessage}
          </Alert>
        </div>
      )}

      <Card noPadding>
        {/* Filters */}
        <div className="flex flex-wrap items-center gap-3 p-4 border-b border-blue-50">
          <Input
            placeholder="Buscar por placa, marca o modelo…"
            leadingIcon={<Search size={13} />}
            className="max-w-xs h-8 text-xs"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Select
            options={STATUS_OPTIONS}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as VehicleStatus | '')}
            className="w-48 h-8 text-xs"
          />
          <Select
            options={TYPE_OPTIONS}
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value as VehicleType | '')}
            className="w-40 h-8 text-xs"
          />
          <Badge variant="default" className="ml-auto">
            {filteredVehicles.length} vehículo{filteredVehicles.length !== 1 ? 's' : ''}
          </Badge>
        </div>

        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Placa</Table.Th>
              <Table.Th>Marca / Modelo</Table.Th>
              <Table.Th>Tipo</Table.Th>
              <Table.Th>Año</Table.Th>
              <Table.Th>Conductor</Table.Th>
              <Table.Th>Registro</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {isLoading ? (
              <Table.Empty
                icon={<RefreshCw size={28} className="animate-spin" />}
                title="Cargando vehículos…"
              />
            ) : fetchError ? (
              <Table.Empty
                icon={<Truck size={36} />}
                title="Error al cargar vehículos"
                description="Verifica tu conexión e intenta de nuevo"
              />
            ) : filteredVehicles.length === 0 ? (
              <Table.Empty
                icon={<Truck size={36} />}
                title="Sin vehículos registrados"
                description={
                  searchQuery || statusFilter || typeFilter
                    ? 'No hay vehículos con los filtros aplicados'
                    : 'Agrega el primer vehículo para comenzar'
                }
                action={
                  !searchQuery && !statusFilter && !typeFilter ? (
                    <Button
                      size="sm"
                      icon={<Plus size={13} />}
                      onClick={() => setVehicleModal({ open: true, editing: null })}
                    >
                      Nuevo vehículo
                    </Button>
                  ) : undefined
                }
              />
            ) : (
              filteredVehicles.map((v) => (
                <Table.Row key={v.id}>
                  <Table.Td>
                    <span className="font-mono font-semibold text-slate-800">{v.plateNumber}</span>
                  </Table.Td>
                  <Table.Td>
                    <p className="font-medium text-slate-800">{v.brand}</p>
                    <p className="text-xs text-slate-400 mt-0.5">{v.vehicleModel}</p>
                  </Table.Td>
                  <Table.Td>
                    <Badge variant="default">{TYPE_LABEL[v.vehicleType]}</Badge>
                  </Table.Td>
                  <Table.Td className="text-slate-500">{v.year ?? '—'}</Table.Td>
                  <Table.Td>
                    {v.assignedDriverId ? (
                      <Badge variant="success" dot>
                        {v.assignedDriverName ?? 'Asignado'}
                      </Badge>
                    ) : (
                      <Badge variant="inactive">Sin conductor</Badge>
                    )}
                  </Table.Td>
                  <Table.Td className="text-slate-400 text-xs">
                    {v.registrationDate ? formatDate(v.registrationDate) : '—'}
                  </Table.Td>
                  <Table.Td>
                    <VehicleStatusBadge status={v.status} />
                  </Table.Td>
                  <Table.Td>
                    <div className="flex items-center gap-1 justify-end">
                      <button
                        title="Editar"
                        onClick={() => setVehicleModal({ open: true, editing: v })}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                      >
                        <MoreHorizontal size={14} />
                      </button>
                      <button
                        title="Cambiar estado"
                        onClick={() => setStatusModal({ open: true, vehicle: v })}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                      >
                        <RefreshCw size={14} />
                      </button>
                      <button
                        title={v.assignedDriverId ? 'Desasignar conductor' : 'Asignar conductor'}
                        onClick={() => setAssignModal({ open: true, vehicle: v })}
                        disabled={v.status !== 'ACTIVE'}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all disabled:opacity-30 disabled:cursor-not-allowed"
                      >
                        {v.assignedDriverId ? <UserMinus size={14} /> : <UserPlus size={14} />}
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
      <VehicleFormModal
        key={vehicleModal.editing?.id ?? 'new'}
        open={vehicleModal.open}
        onClose={() => setVehicleModal({ open: false, editing: null })}
        editing={vehicleModal.editing}
        onSuccess={() =>
          showSuccess(
            vehicleModal.editing
              ? 'Vehículo actualizado correctamente'
              : 'Vehículo registrado correctamente',
          )
        }
      />

      <ChangeStatusModal
        key={`status-${statusModal.vehicle?.id ?? 'none'}`}
        open={statusModal.open}
        onClose={() => setStatusModal({ open: false, vehicle: null })}
        vehicle={statusModal.vehicle}
        onSuccess={() => showSuccess('Estado del vehículo actualizado correctamente')}
      />

      <AssignDriverModal
        key={`assign-${assignModal.vehicle?.id ?? 'none'}`}
        open={assignModal.open}
        onClose={() => setAssignModal({ open: false, vehicle: null })}
        vehicle={assignModal.vehicle}
        onSuccess={() =>
          showSuccess(
            assignModal.vehicle?.assignedDriverId
              ? 'Conductor desasignado correctamente'
              : 'Conductor asignado correctamente',
          )
        }
      />
    </Page>
  )
}
