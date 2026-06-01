import { useState, useCallback, useMemo } from 'react'
import { Plus, FileText, RefreshCw, Search, AlertTriangle, RotateCcw, PowerOff } from 'lucide-react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Page } from '@/components/layout'
import {
  Button, Card, Table, DocumentStatusBadge, Badge,
  Input, Select, Textarea, Modal, ConfirmModal, Alert,
} from '@/components/ui'
import { formatDate } from '@/lib/utils'
import { useAuth } from '@/context/AuthContext'
import type { Document, DocumentType, DocumentStatus, Driver, Vehicle } from '@/types/domain'
import {
  listDocuments, getDocumentAlerts, createDocument, renewDocument, deactivateDocument,
  type CreateDocumentPayload, type RenewDocumentPayload,
} from '@/services/documents'
import { listDrivers } from '@/services/drivers'
import { listVehicles } from '@/services/vehicles'

// ─── Constants ────────────────────────────────────────────────────────────────

const DOC_TYPE_LABEL: Record<DocumentType, string> = {
  SOAT: 'SOAT',
  RTM: 'Rev. técnico-mecánica',
  OPERATION_CARD: 'Tarjeta de operación',
  PROPERTY_CARD: 'Tarjeta de propiedad',
  LIABILITY_POLICY: 'Póliza RC',
  DRIVING_LICENSE: 'Licencia de conducción',
  MEDICAL_CERTIFICATE: 'Certificado médico',
  EMPLOYMENT_CONTRACT: 'Contrato de vinculación',
  TRAFFIC_CLEARANCE: 'Paz y salvo tránsito',
  COMPANY_LICENSE: 'Habilitación empresa',
  ROUTE_CONTRACT: 'Contrato de ruta',
  OTHER_DRIVER: 'Otro (conductor)',
  OTHER_VEHICLE: 'Otro (vehículo)',
  OTHER_ADMIN: 'Otro (administrativo)',
}

type DocTypeOption = { value: DocumentType; label: string }

const VEHICLE_DOC_TYPES: DocTypeOption[] = [
  { value: 'SOAT', label: 'SOAT' },
  { value: 'RTM', label: 'Rev. técnico-mecánica' },
  { value: 'OPERATION_CARD', label: 'Tarjeta de operación' },
  { value: 'PROPERTY_CARD', label: 'Tarjeta de propiedad' },
  { value: 'LIABILITY_POLICY', label: 'Póliza RC' },
  { value: 'OTHER_VEHICLE', label: 'Otro' },
]

const DRIVER_DOC_TYPES: DocTypeOption[] = [
  { value: 'DRIVING_LICENSE', label: 'Licencia de conducción' },
  { value: 'MEDICAL_CERTIFICATE', label: 'Certificado médico' },
  { value: 'EMPLOYMENT_CONTRACT', label: 'Contrato de vinculación' },
  { value: 'TRAFFIC_CLEARANCE', label: 'Paz y salvo tránsito' },
  { value: 'OTHER_DRIVER', label: 'Otro' },
]

const ADMIN_DOC_TYPES: DocTypeOption[] = [
  { value: 'COMPANY_LICENSE', label: 'Habilitación empresa' },
  { value: 'ROUTE_CONTRACT', label: 'Contrato de ruta' },
  { value: 'OTHER_ADMIN', label: 'Otro' },
]

const TYPE_FILTER_OPTIONS = [
  { value: '', label: 'Todos los tipos' },
  ...Object.entries(DOC_TYPE_LABEL).map(([value, label]) => ({ value, label })),
]

const STATUS_FILTER_OPTIONS = [
  { value: '', label: 'Todos los estados' },
  { value: 'VALID', label: 'Vigente' },
  { value: 'EXPIRING_SOON', label: 'Por vencer' },
  { value: 'EXPIRED', label: 'Vencido' },
  { value: 'INACTIVE', label: 'Inactivo' },
  { value: 'NO_EXPIRY', label: 'Sin vencimiento' },
]

const ENTITY_KIND_OPTIONS = [
  { value: '', label: 'Todas las entidades' },
  { value: 'vehicle', label: 'Vehículos' },
  { value: 'driver', label: 'Conductores' },
  { value: 'admin', label: 'Administrativos' },
]

const FILE_FORMAT_OPTIONS = [
  { value: '', label: 'Sin archivo' },
  { value: 'PDF', label: 'PDF' },
  { value: 'JPG', label: 'JPG' },
  { value: 'PNG', label: 'PNG' },
]

// ─── Entity cell ──────────────────────────────────────────────────────────────

interface EntityCellProps {
  doc: Document
  driverMap: Record<string, Driver>
  vehicleMap: Record<string, Vehicle>
}

function EntityCell({ doc, driverMap, vehicleMap }: EntityCellProps) {
  if (doc.driverId) {
    const d = driverMap[doc.driverId]
    return d ? (
      <div>
        <p className="text-sm text-slate-700 font-medium">{d.firstName} {d.lastName}</p>
        <p className="text-xs text-slate-400 mt-0.5">Conductor</p>
      </div>
    ) : (
      <span className="text-xs font-mono text-slate-400">{doc.driverId}</span>
    )
  }
  if (doc.vehicleId) {
    const v = vehicleMap[doc.vehicleId]
    return v ? (
      <div>
        <p className="text-sm text-slate-700 font-medium">{v.plateNumber}</p>
        <p className="text-xs text-slate-400 mt-0.5">{v.brand} {v.vehicleModel}</p>
      </div>
    ) : (
      <span className="text-xs font-mono text-slate-400">{doc.vehicleId}</span>
    )
  }
  return <span className="text-xs text-slate-400">Administrativo</span>
}

// ─── Create document modal ────────────────────────────────────────────────────

type EntityKind = 'vehicle' | 'driver' | 'admin'

interface DocFormValues {
  entityKind: EntityKind
  driverId: string
  vehicleId: string
  name: string
  documentType: DocumentType | ''
  referenceNumber: string
  issuingEntity: string
  issueDate: string
  expiryDate: string
  fileUrl: string
  fileFormat: string
  notes: string
}

const EMPTY_DOC_FORM: DocFormValues = {
  entityKind: 'vehicle',
  driverId: '',
  vehicleId: '',
  name: '',
  documentType: '',
  referenceNumber: '',
  issuingEntity: '',
  issueDate: '',
  expiryDate: '',
  fileUrl: '',
  fileFormat: '',
  notes: '',
}

interface DocFormModalProps {
  open: boolean
  onClose: () => void
  onSuccess: () => void
  uploadedBy: string
  drivers: Driver[]
  vehicles: Vehicle[]
}

function DocFormModal({ open, onClose, onSuccess, uploadedBy, drivers, vehicles }: DocFormModalProps) {
  const [form, setForm] = useState<DocFormValues>(EMPTY_DOC_FORM)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const setField = (field: keyof DocFormValues) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleEntityKindChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const kind = e.target.value as EntityKind
    setForm((prev) => ({ ...prev, entityKind: kind, driverId: '', vehicleId: '', documentType: '' }))
  }

  const typeOptions = useMemo((): DocTypeOption[] => {
    if (form.entityKind === 'vehicle') return VEHICLE_DOC_TYPES
    if (form.entityKind === 'driver') return DRIVER_DOC_TYPES
    return ADMIN_DOC_TYPES
  }, [form.entityKind])

  const driverOptions = useMemo(
    () => drivers.map((d) => ({ value: d.id, label: `${d.firstName} ${d.lastName}` })),
    [drivers],
  )

  const vehicleOptions = useMemo(
    () => vehicles.map((v) => ({ value: v.id, label: `${v.plateNumber} — ${v.brand} ${v.vehicleModel}` })),
    [vehicles],
  )

  const handleSubmit = async () => {
    if (!form.name.trim()) { setError('El nombre del documento es obligatorio.'); return }
    if (!form.documentType) { setError('Selecciona el tipo de documento.'); return }
    if (form.entityKind === 'driver' && !form.driverId) { setError('Selecciona el conductor asociado.'); return }
    if (form.entityKind === 'vehicle' && !form.vehicleId) { setError('Selecciona el vehículo asociado.'); return }

    setError(null)
    setLoading(true)
    try {
      const payload: CreateDocumentPayload = {
        name: form.name.trim(),
        documentType: form.documentType as DocumentType,
        referenceNumber: form.referenceNumber || undefined,
        issuingEntity: form.issuingEntity || undefined,
        issueDate: form.issueDate || undefined,
        expiryDate: form.expiryDate || undefined,
        fileUrl: form.fileUrl || undefined,
        fileFormat: form.fileFormat || undefined,
        notes: form.notes || undefined,
        driverId: form.entityKind === 'driver' ? form.driverId : undefined,
        vehicleId: form.entityKind === 'vehicle' ? form.vehicleId : undefined,
        uploadedBy,
      }
      await createDocument(payload)
      setForm(EMPTY_DOC_FORM)
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al registrar el documento. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Nuevo documento"
      description="Registra un documento digital con sus fechas de vigencia"
      size="xl"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
          <Button onClick={handleSubmit} loading={loading}>Registrar documento</Button>
        </>
      }
    >
      {error && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
        </div>
      )}

      <div className="grid grid-cols-2 gap-4">
        {/* Entity kind selector */}
        <Select
          label="Pertenece a"
          value={form.entityKind}
          onChange={handleEntityKindChange}
          options={[
            { value: 'vehicle', label: 'Vehículo' },
            { value: 'driver', label: 'Conductor' },
            { value: 'admin', label: 'Administrativo' },
          ]}
          required
        />

        {form.entityKind === 'vehicle' && (
          <Select
            label="Vehículo"
            value={form.vehicleId}
            onChange={setField('vehicleId')}
            options={[{ value: '', label: 'Selecciona un vehículo…' }, ...vehicleOptions]}
            required
          />
        )}
        {form.entityKind === 'driver' && (
          <Select
            label="Conductor"
            value={form.driverId}
            onChange={setField('driverId')}
            options={[{ value: '', label: 'Selecciona un conductor…' }, ...driverOptions]}
            required
          />
        )}
        {form.entityKind === 'admin' && <div />}

        {/* Document type */}
        <Select
          label="Tipo de documento"
          value={form.documentType}
          onChange={setField('documentType')}
          options={[{ value: '', label: 'Selecciona el tipo…' }, ...typeOptions]}
          required
        />

        {/* Name */}
        <Input
          label="Nombre del documento"
          value={form.name}
          onChange={setField('name')}
          placeholder="Ej. SOAT Bus ABC-123"
          required
        />

        {/* Reference number */}
        <Input
          label="Número de referencia"
          value={form.referenceNumber}
          onChange={setField('referenceNumber')}
          placeholder="Código o número del documento"
        />

        {/* Issuing entity */}
        <Input
          label="Entidad emisora"
          value={form.issuingEntity}
          onChange={setField('issuingEntity')}
          placeholder="Ej. Seguros Bolívar"
        />

        {/* Dates */}
        <Input
          label="Fecha de emisión"
          type="date"
          value={form.issueDate}
          onChange={setField('issueDate')}
        />
        <Input
          label="Fecha de vencimiento"
          type="date"
          value={form.expiryDate}
          onChange={setField('expiryDate')}
          hint="Deja vacío si el documento no vence"
        />

        {/* File */}
        <Input
          label="URL del archivo"
          value={form.fileUrl}
          onChange={setField('fileUrl')}
          placeholder="https://…"
        />
        <Select
          label="Formato del archivo"
          value={form.fileFormat}
          onChange={setField('fileFormat')}
          options={FILE_FORMAT_OPTIONS}
        />

        {/* Notes */}
        <div className="col-span-2">
          <Textarea
            label="Observaciones"
            value={form.notes}
            onChange={setField('notes')}
            placeholder="Notas adicionales sobre el documento…"
          />
        </div>
      </div>
    </Modal>
  )
}

// ─── Renew document modal ─────────────────────────────────────────────────────

interface RenewDocModalProps {
  open: boolean
  onClose: () => void
  onSuccess: () => void
  doc: Document | null
  replacedBy: string
}

function RenewDocModal({ open, onClose, onSuccess, doc, replacedBy }: RenewDocModalProps) {
  const [newExpiryDate, setNewExpiryDate] = useState('')
  const [newFileUrl, setNewFileUrl] = useState('')
  const [newFileFormat, setNewFileFormat] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async () => {
    if (!newExpiryDate) { setError('La nueva fecha de vencimiento es obligatoria.'); return }
    if (!doc) return

    setError(null)
    setLoading(true)
    try {
      const payload: RenewDocumentPayload = {
        newExpiryDate,
        newFileUrl: newFileUrl || undefined,
        newFileFormat: newFileFormat || undefined,
        replacedBy,
      }
      await renewDocument(doc.id, payload)
      setNewExpiryDate('')
      setNewFileUrl('')
      setNewFileFormat('')
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      if (Array.isArray(data)) setError(data[0])
      else if (typeof data === 'string') setError(data)
      else setError('Error al renovar el documento. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Renovar documento"
      description={doc ? `Renovando: ${doc.name}` : ''}
      size="md"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
          <Button onClick={handleSubmit} loading={loading}>Renovar documento</Button>
        </>
      }
    >
      {error && (
        <div className="mb-4">
          <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>
        </div>
      )}

      <div className="grid grid-cols-1 gap-4">
        {doc && (
          <div className="bg-blue-50 rounded-lg px-4 py-3 text-xs text-slate-500">
            <p><span className="font-semibold">Vencimiento actual:</span> {formatDate(doc.expiryDate) ?? 'Sin vencimiento'}</p>
            {doc.referenceNumber && <p className="mt-1"><span className="font-semibold">Referencia:</span> {doc.referenceNumber}</p>}
          </div>
        )}

        <Input
          label="Nueva fecha de vencimiento"
          type="date"
          value={newExpiryDate}
          onChange={(e) => setNewExpiryDate(e.target.value)}
          required
        />

        <Input
          label="URL del nuevo archivo"
          value={newFileUrl}
          onChange={(e) => setNewFileUrl(e.target.value)}
          placeholder="https://… (opcional)"
        />

        <Select
          label="Formato del nuevo archivo"
          value={newFileFormat}
          onChange={(e) => setNewFileFormat(e.target.value)}
          options={FILE_FORMAT_OPTIONS}
        />

        <p className="text-xs text-slate-400">
          La versión anterior del documento quedará guardada en el historial.
        </p>
      </div>
    </Modal>
  )
}

// ─── Detail modal ─────────────────────────────────────────────────────────────

interface DetailModalProps {
  open: boolean
  onClose: () => void
  doc: Document | null
  driverMap: Record<string, Driver>
  vehicleMap: Record<string, Vehicle>
  onRenew: () => void
  onDeactivate: () => void
}

function DetailModal({ open, onClose, doc, driverMap, vehicleMap, onRenew, onDeactivate }: DetailModalProps) {
  if (!doc) return null

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={doc.name}
      description={DOC_TYPE_LABEL[doc.documentType]}
      size="lg"
      footer={
        <div className="flex items-center gap-2 w-full">
          <div className="flex-1 flex gap-2">
            {doc.status !== 'INACTIVE' && (
              <>
                <Button variant="outline" size="sm" icon={<RotateCcw size={13} />} onClick={onRenew}>
                  Renovar
                </Button>
                <Button variant="danger" size="sm" icon={<PowerOff size={13} />} onClick={onDeactivate}>
                  Desactivar
                </Button>
              </>
            )}
          </div>
          <Button variant="outline" onClick={onClose}>Cerrar</Button>
        </div>
      }
    >
      <div className="grid grid-cols-2 gap-x-8 gap-y-3 text-sm">
        <DetailRow label="Estado">
          <DocumentStatusBadge status={doc.status} />
        </DetailRow>
        <DetailRow label="Tipo">
          <Badge variant="default">{DOC_TYPE_LABEL[doc.documentType]}</Badge>
        </DetailRow>
        {doc.referenceNumber && (
          <DetailRow label="Referencia">
            <span className="font-mono text-xs text-slate-600">{doc.referenceNumber}</span>
          </DetailRow>
        )}
        {doc.issuingEntity && (
          <DetailRow label="Entidad emisora">
            <span className="text-slate-600">{doc.issuingEntity}</span>
          </DetailRow>
        )}
        <DetailRow label="Fecha de emisión">
          <span className="text-slate-600">{formatDate(doc.issueDate) ?? '—'}</span>
        </DetailRow>
        <DetailRow label="Fecha de vencimiento">
          <span className={doc.status === 'EXPIRED' ? 'text-err font-medium' : 'text-slate-600'}>
            {formatDate(doc.expiryDate) ?? 'Sin vencimiento'}
          </span>
        </DetailRow>
        <DetailRow label="Entidad asociada">
          <EntityCell doc={doc} driverMap={driverMap} vehicleMap={vehicleMap} />
        </DetailRow>
        <DetailRow label="Cargado por">
          <span className="text-slate-600">{doc.uploadedBy}</span>
        </DetailRow>
        <DetailRow label="Fecha de carga">
          <span className="text-slate-600">{formatDate(doc.uploadDate)}</span>
        </DetailRow>
        {doc.fileUrl && (
          <div className="col-span-2">
            <DetailRow label="Archivo">
              <a
                href={doc.fileUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-500 hover:underline text-xs font-mono"
              >
                {doc.fileFormat ? `[${doc.fileFormat}] ` : ''}{doc.fileUrl}
              </a>
            </DetailRow>
          </div>
        )}
        {doc.notes && (
          <div className="col-span-2">
            <DetailRow label="Observaciones">
              <span className="text-slate-500 text-xs leading-relaxed">{doc.notes}</span>
            </DetailRow>
          </div>
        )}
      </div>
    </Modal>
  )
}

function DetailRow({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide mb-1">{label}</p>
      <div>{children}</div>
    </div>
  )
}

// ─── Main page ────────────────────────────────────────────────────────────────

export function DocumentsPage() {
  const { user } = useAuth()
  const queryClient = useQueryClient()

  const [typeFilter, setTypeFilter] = useState<DocumentType | ''>('')
  const [statusFilter, setStatusFilter] = useState<DocumentStatus | ''>('')
  const [entityKindFilter, setEntityKindFilter] = useState<'vehicle' | 'driver' | 'admin' | ''>('')
  const [searchQuery, setSearchQuery] = useState('')

  const [createOpen, setCreateOpen] = useState(false)
  const [detailDoc, setDetailDoc] = useState<Document | null>(null)
  const [renewDoc, setRenewDoc] = useState<Document | null>(null)
  const [deactivateDoc, setDeactivateDoc] = useState<Document | null>(null)
  const [deactivatingId, setDeactivatingId] = useState<string | null>(null)

  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const invalidate = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['documents'] })
  }, [queryClient])

  const { data: documents = [], isLoading, error: fetchError } = useQuery({
    queryKey: ['documents', typeFilter, statusFilter],
    queryFn: () =>
      listDocuments({
        type: typeFilter || undefined,
        status: statusFilter || undefined,
      }),
  })

  const { data: alerts = [] } = useQuery({
    queryKey: ['documents', 'alerts'],
    queryFn: () => getDocumentAlerts(),
    staleTime: 60_000,
  })

  const { data: drivers = [] } = useQuery({
    queryKey: ['drivers'],
    queryFn: () => listDrivers(),
    staleTime: 5 * 60_000,
  })

  const { data: vehicles = [] } = useQuery({
    queryKey: ['vehicles'],
    queryFn: () => listVehicles(),
    staleTime: 5 * 60_000,
  })

  const driverMap = useMemo(
    () => Object.fromEntries(drivers.map((d) => [d.id, d])),
    [drivers],
  )
  const vehicleMap = useMemo(
    () => Object.fromEntries(vehicles.map((v) => [v.id, v])),
    [vehicles],
  )

  const filteredDocuments = useMemo(() => {
    return documents.filter((doc) => {
      if (entityKindFilter === 'driver' && !doc.driverId) return false
      if (entityKindFilter === 'vehicle' && !doc.vehicleId) return false
      if (entityKindFilter === 'admin' && (doc.driverId || doc.vehicleId)) return false

      if (!searchQuery) return true
      const q = searchQuery.toLowerCase()
      return (
        doc.name.toLowerCase().includes(q) ||
        (doc.referenceNumber?.toLowerCase().includes(q) ?? false) ||
        (doc.issuingEntity?.toLowerCase().includes(q) ?? false)
      )
    })
  }, [documents, entityKindFilter, searchQuery])

  const expiredCount = alerts.filter((d) => d.status === 'EXPIRED').length
  const expiringSoonCount = alerts.filter((d) => d.status === 'EXPIRING_SOON').length

  const handleDeactivate = async () => {
    if (!deactivateDoc) return
    setDeactivatingId(deactivateDoc.id)
    try {
      await deactivateDocument(deactivateDoc.id)
      setSuccessMessage(`Documento "${deactivateDoc.name}" marcado como inactivo`)
      setDetailDoc(null)
      invalidate()
    } catch {
      setSuccessMessage(null)
    } finally {
      setDeactivatingId(null)
      setDeactivateDoc(null)
    }
  }

  return (
    <Page
      title="Documentos"
      subtitle="Gestión documental y alertas de vencimiento"
      actions={
        <Button icon={<Plus size={14} />} onClick={() => setCreateOpen(true)}>
          Nuevo documento
        </Button>
      }
    >
      {successMessage && (
        <div className="mb-4">
          <Alert variant="success" onClose={() => setSuccessMessage(null)}>{successMessage}</Alert>
        </div>
      )}

      {/* Alerts banner */}
      {(expiredCount > 0 || expiringSoonCount > 0) && (
        <div className="mb-4 flex flex-col gap-2">
          {expiredCount > 0 && (
            <Alert variant="error">
              <span className="flex items-center gap-1.5">
                <AlertTriangle size={14} />
                {expiredCount} documento{expiredCount !== 1 ? 's' : ''} vencido{expiredCount !== 1 ? 's' : ''} — requieren atención inmediata
              </span>
            </Alert>
          )}
          {expiringSoonCount > 0 && (
            <Alert variant="warning">
              <span className="flex items-center gap-1.5">
                <AlertTriangle size={14} />
                {expiringSoonCount} documento{expiringSoonCount !== 1 ? 's' : ''} próximo{expiringSoonCount !== 1 ? 's' : ''} a vencer en los próximos 30 días
              </span>
            </Alert>
          )}
        </div>
      )}

      <Card noPadding>
        {/* Filters */}
        <div className="flex flex-wrap items-center gap-3 p-4 border-b border-blue-50">
          <Input
            placeholder="Buscar por nombre, referencia o emisor…"
            leadingIcon={<Search size={13} />}
            className="max-w-xs h-8 text-xs"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Select
            options={TYPE_FILTER_OPTIONS}
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value as DocumentType | '')}
            className="w-48 h-8 text-xs"
          />
          <Select
            options={STATUS_FILTER_OPTIONS}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as DocumentStatus | '')}
            className="w-44 h-8 text-xs"
          />
          <Select
            options={ENTITY_KIND_OPTIONS}
            value={entityKindFilter}
            onChange={(e) => setEntityKindFilter(e.target.value as typeof entityKindFilter)}
            className="w-44 h-8 text-xs"
          />
          <Badge variant="default" className="ml-auto">
            {filteredDocuments.length} documento{filteredDocuments.length !== 1 ? 's' : ''}
          </Badge>
        </div>

        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Documento</Table.Th>
              <Table.Th>Tipo</Table.Th>
              <Table.Th>Entidad</Table.Th>
              <Table.Th>Emisor</Table.Th>
              <Table.Th>Vencimiento</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {isLoading ? (
              <Table.Empty
                icon={<RefreshCw size={28} className="animate-spin" />}
                title="Cargando documentos…"
              />
            ) : fetchError ? (
              <Table.Empty
                icon={<FileText size={36} />}
                title="Error al cargar documentos"
                description="Verifica tu conexión e intenta de nuevo"
              />
            ) : filteredDocuments.length === 0 ? (
              <Table.Empty
                icon={<FileText size={36} />}
                title="Sin documentos registrados"
                description={
                  searchQuery || typeFilter || statusFilter || entityKindFilter
                    ? 'No hay documentos con los filtros aplicados'
                    : 'Registra el primer documento para comenzar'
                }
                action={
                  !searchQuery && !typeFilter && !statusFilter && !entityKindFilter ? (
                    <Button
                      size="sm"
                      icon={<Plus size={13} />}
                      onClick={() => setCreateOpen(true)}
                    >
                      Nuevo documento
                    </Button>
                  ) : undefined
                }
              />
            ) : (
              filteredDocuments.map((doc) => (
                <Table.Row key={doc.id}>
                  <Table.Td>
                    <p className="font-medium text-slate-800">{doc.name}</p>
                    {doc.referenceNumber && (
                      <p className="text-xs font-mono text-slate-400 mt-0.5">{doc.referenceNumber}</p>
                    )}
                  </Table.Td>
                  <Table.Td>
                    <Badge variant="default">{DOC_TYPE_LABEL[doc.documentType]}</Badge>
                  </Table.Td>
                  <Table.Td>
                    <EntityCell doc={doc} driverMap={driverMap} vehicleMap={vehicleMap} />
                  </Table.Td>
                  <Table.Td className="text-slate-500 text-sm">{doc.issuingEntity ?? '—'}</Table.Td>
                  <Table.Td className="text-slate-500 text-sm">
                    {doc.expiryDate ? (
                      <span className={doc.status === 'EXPIRED' ? 'text-err font-medium' : doc.status === 'EXPIRING_SOON' ? 'text-warn font-medium' : ''}>
                        {formatDate(doc.expiryDate)}
                      </span>
                    ) : (
                      <span className="text-slate-300">—</span>
                    )}
                  </Table.Td>
                  <Table.Td>
                    <DocumentStatusBadge status={doc.status} />
                  </Table.Td>
                  <Table.Td>
                    <div className="flex items-center gap-1 justify-end">
                      <button
                        title="Ver detalle"
                        onClick={() => setDetailDoc(doc)}
                        className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                      >
                        <FileText size={14} />
                      </button>
                      {doc.status !== 'INACTIVE' && (
                        <>
                          <button
                            title="Renovar documento"
                            onClick={() => setRenewDoc(doc)}
                            className="p-1.5 rounded-lg text-slate-300 hover:text-ok hover:bg-ok-bg transition-all"
                          >
                            <RotateCcw size={14} />
                          </button>
                          <button
                            title="Marcar como inactivo"
                            onClick={() => setDeactivateDoc(doc)}
                            className="p-1.5 rounded-lg text-slate-300 hover:text-err hover:bg-err-bg transition-all"
                          >
                            <PowerOff size={14} />
                          </button>
                        </>
                      )}
                    </div>
                  </Table.Td>
                </Table.Row>
              ))
            )}
          </Table.Body>
        </Table>
      </Card>

      {/* Modals */}
      <DocFormModal
        key={createOpen ? 'open' : 'closed'}
        open={createOpen}
        onClose={() => setCreateOpen(false)}
        onSuccess={() => {
          setSuccessMessage('Documento registrado correctamente')
          invalidate()
        }}
        uploadedBy={user?.username ?? ''}
        drivers={drivers}
        vehicles={vehicles}
      />

      <DetailModal
        open={detailDoc !== null}
        onClose={() => setDetailDoc(null)}
        doc={detailDoc}
        driverMap={driverMap}
        vehicleMap={vehicleMap}
        onRenew={() => {
          setRenewDoc(detailDoc)
          setDetailDoc(null)
        }}
        onDeactivate={() => {
          setDeactivateDoc(detailDoc)
          setDetailDoc(null)
        }}
      />

      <RenewDocModal
        key={renewDoc?.id ?? 'no-renew'}
        open={renewDoc !== null}
        onClose={() => setRenewDoc(null)}
        onSuccess={() => {
          setSuccessMessage(`Documento "${renewDoc?.name}" renovado correctamente`)
          invalidate()
        }}
        doc={renewDoc}
        replacedBy={user?.username ?? ''}
      />

      <ConfirmModal
        open={deactivateDoc !== null}
        onClose={() => setDeactivateDoc(null)}
        onConfirm={handleDeactivate}
        title="Desactivar documento"
        message={`¿Marcar "${deactivateDoc?.name}" como inactivo? El documento dejará de aparecer en los reportes de vigencia.`}
        confirmLabel="Desactivar"
        confirmVariant="danger"
        loading={deactivatingId === deactivateDoc?.id}
      />
    </Page>
  )
}
