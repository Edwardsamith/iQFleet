import { useState, useCallback } from 'react'
import {
  Search, Plus, Users, UserCheck, UserX, MoreHorizontal,
  KeyRound, RefreshCw, ShieldCheck, ShieldX,
} from 'lucide-react'
import { Page } from '@/components/layout'
import {
  Button, Card, Table, UserStatusBadge, Input, Select, Badge,
  Modal, ConfirmModal, Alert,
} from '@/components/ui'
import { formatDate } from '@/lib/utils'
import type { UserStatus } from '@/types/domain'
import {
  listUsers, listPendingRequests, createUser, updateUser,
  changeUserStatus, adminResetPassword, approveRequest, rejectRequest,
  type UserDetail,
} from '@/services/users'
import { useQuery, useQueryClient } from '@tanstack/react-query'

// ─── Constants ────────────────────────────────────────────────────────────────

const ROLE_LABELS: Record<string, string> = {
  ROLE_OWNER: 'Propietario',
  ROLE_ADMIN: 'Administrador',
}

const STATUS_OPTIONS = [
  { value: '', label: 'Todos los estados' },
  { value: 'ACTIVE', label: 'Activo' },
  { value: 'INACTIVE', label: 'Inactivo' },
  { value: 'PENDING', label: 'Pendiente' },
  { value: 'BLOCKED', label: 'Bloqueado' },
]

const ROLE_OPTIONS = [
  { value: '', label: 'Todos los roles' },
  { value: 'ROLE_OWNER', label: 'Propietario' },
  { value: 'ROLE_ADMIN', label: 'Administrador' },
]

const ID_TYPE_OPTIONS = [
  { value: 'CC', label: 'Cédula de ciudadanía (CC)' },
  { value: 'CE', label: 'Cédula de extranjería (CE)' },
  { value: 'PA', label: 'Pasaporte (PA)' },
]

const ROLE_FORM_OPTIONS = [
  { value: 'ROLE_ADMIN', label: 'Administrador' },
  { value: 'ROLE_OWNER', label: 'Propietario' },
]

// ─── User form ────────────────────────────────────────────────────────────────

interface UserFormValues {
  firstName: string
  lastName: string
  identificationType: string
  identificationNumber: string
  email: string
  phone: string
  username: string
  password: string
  role: string
  status: string
}

const EMPTY_FORM: UserFormValues = {
  firstName: '', lastName: '', identificationType: 'CC',
  identificationNumber: '', email: '', phone: '',
  username: '', password: '', role: 'ROLE_ADMIN', status: 'ACTIVE',
}

interface UserFormModalProps {
  open: boolean
  onClose: () => void
  editing: UserDetail | null
  onSuccess: () => void
}

function UserFormModal({ open, onClose, editing, onSuccess }: UserFormModalProps) {
  const [form, setForm] = useState<UserFormValues>(
    editing
      ? {
          firstName: editing.firstName,
          lastName: editing.lastName,
          identificationType: editing.identificationType ?? 'CC',
          identificationNumber: editing.identificationNumber ?? '',
          email: editing.email,
          phone: editing.phone ?? '',
          username: editing.username,
          password: '',
          role: editing.role,
          status: editing.status,
        }
      : EMPTY_FORM,
  )
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const setField = (field: keyof UserFormValues) => (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async () => {
    setError(null)
    setLoading(true)
    try {
      if (editing) {
        await updateUser(editing.id, {
          firstName: form.firstName,
          lastName: form.lastName,
          phone: form.phone,
          email: form.email,
          role: form.role,
        })
      } else {
        await createUser({
          firstName: form.firstName,
          lastName: form.lastName,
          identificationType: form.identificationType,
          identificationNumber: form.identificationNumber,
          email: form.email,
          phone: form.phone,
          username: form.username,
          password: form.password,
          role: form.role,
          status: form.status,
        })
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
      title={editing ? 'Editar usuario' : 'Nuevo usuario'}
      description={editing ? `Editando @${editing.username}` : 'Completa los datos del nuevo usuario'}
      size="lg"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
          <Button onClick={handleSubmit} loading={loading}>
            {editing ? 'Guardar cambios' : 'Crear usuario'}
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
        <Input label="Nombres" value={form.firstName} onChange={setField('firstName')} required />
        <Input label="Apellidos" value={form.lastName} onChange={setField('lastName')} required />

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
            />
          </>
        )}

        <Input
          label="Correo electrónico"
          type="email"
          value={form.email}
          onChange={setField('email')}
          required
          disabled={!!editing}
        />
        <Input label="Teléfono" type="tel" value={form.phone} onChange={setField('phone')} />

        {!editing && (
          <>
            <Input
              label="Nombre de usuario"
              value={form.username}
              onChange={setField('username')}
              required
            />
            <Input
              label="Contraseña"
              type="password"
              value={form.password}
              onChange={setField('password')}
              required
              placeholder="Mín. 8 chars, 1 mayúscula, 1 número"
            />
          </>
        )}

        <Select
          label="Rol"
          value={form.role}
          onChange={setField('role')}
          options={ROLE_FORM_OPTIONS}
          required
        />
        <Select
          label="Estado"
          value={form.status}
          onChange={setField('status')}
          options={[
            { value: 'ACTIVE', label: 'Activo' },
            { value: 'INACTIVE', label: 'Inactivo' },
          ]}
          required
        />
      </div>
    </Modal>
  )
}

// ─── Reset password modal ─────────────────────────────────────────────────────

function ResetPasswordModal({
  open, onClose, userId, onSuccess,
}: { open: boolean; onClose: () => void; userId: string; onSuccess: () => void }) {
  const [newPassword, setNewPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async () => {
    setError(null)
    if (newPassword.length < 8 || !/[A-Z]/.test(newPassword) || !/[0-9]/.test(newPassword)) {
      setError('Mín. 8 caracteres, 1 mayúscula, 1 número')
      return
    }
    setLoading(true)
    try {
      await adminResetPassword(userId, newPassword)
      onSuccess()
      onClose()
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: string[] | string } }
      const data = axiosErr?.response?.data
      setError(Array.isArray(data) ? data[0] : 'Error al restablecer contraseña')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Restablecer contraseña"
      description="El usuario deberá usar esta contraseña al próximo inicio de sesión"
      size="sm"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
          <Button variant="danger" onClick={handleSubmit} loading={loading}>Restablecer</Button>
        </>
      }
    >
      {error && <div className="mb-3"><Alert variant="error" onClose={() => setError(null)}>{error}</Alert></div>}
      <Input
        label="Nueva contraseña"
        type="password"
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        required
        placeholder="Mín. 8 chars, 1 mayúscula, 1 número"
      />
    </Modal>
  )
}

// ─── Reject request modal ─────────────────────────────────────────────────────

function RejectRequestModal({
  open, onClose, solicitudId, onSuccess,
}: { open: boolean; onClose: () => void; solicitudId: string; onSuccess: () => void }) {
  const [reason, setReason] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async () => {
    setError(null)
    setLoading(true)
    try {
      await rejectRequest(solicitudId, reason)
      onSuccess()
      onClose()
    } catch {
      setError('Error al rechazar la solicitud')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Rechazar solicitud"
      size="sm"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>Cancelar</Button>
          <Button variant="danger" onClick={handleSubmit} loading={loading}>Rechazar</Button>
        </>
      }
    >
      {error && <div className="mb-3"><Alert variant="error" onClose={() => setError(null)}>{error}</Alert></div>}
      <Input
        label="Motivo de rechazo (opcional)"
        value={reason}
        onChange={(e) => setReason(e.target.value)}
        placeholder="Ej. Información incompleta"
      />
    </Modal>
  )
}

// ─── Main page ────────────────────────────────────────────────────────────────

type ActiveTab = 'users' | 'requests'

export function UsersPage() {
  const queryClient = useQueryClient()
  const [activeTab, setActiveTab] = useState<ActiveTab>('users')
  const [statusFilter, setStatusFilter] = useState('')
  const [roleFilter, setRoleFilter] = useState('')
  const [searchQuery, setSearchQuery] = useState('')
  const [userModal, setUserModal] = useState<{ open: boolean; editing: UserDetail | null }>({
    open: false,
    editing: null,
  })
  const [resetModal, setResetModal] = useState<{ open: boolean; userId: string }>({
    open: false,
    userId: '',
  })
  const [statusConfirm, setStatusConfirm] = useState<{
    open: boolean; userId: string; newStatus: string; label: string
  }>({ open: false, userId: '', newStatus: '', label: '' })
  const [rejectModal, setRejectModal] = useState<{ open: boolean; solicitudId: string }>({
    open: false,
    solicitudId: '',
  })
  const [pendingActionId, setPendingActionId] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const invalidateAll = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['users'] })
    queryClient.invalidateQueries({ queryKey: ['requests'] })
  }, [queryClient])

  const { data: users = [], isLoading: loadingUsers, error: usersError } = useQuery({
    queryKey: ['users', statusFilter, roleFilter],
    queryFn: () => listUsers({ status: statusFilter || undefined, role: roleFilter || undefined }),
  })

  const { data: requests = [], isLoading: loadingRequests } = useQuery({
    queryKey: ['requests'],
    queryFn: listPendingRequests,
  })

  const filteredUsers = users.filter((user) => {
    if (!searchQuery) return true
    const query = searchQuery.toLowerCase()
    return (
      user.firstName.toLowerCase().includes(query) ||
      user.lastName.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query) ||
      user.username.toLowerCase().includes(query)
    )
  })

  const handleChangeStatus = async () => {
    setPendingActionId(statusConfirm.userId)
    try {
      await changeUserStatus(statusConfirm.userId, statusConfirm.newStatus)
      setSuccessMessage(`Usuario ${statusConfirm.label.toLowerCase()} correctamente`)
      invalidateAll()
    } catch {
      // error handled by UI
    } finally {
      setPendingActionId(null)
      setStatusConfirm({ open: false, userId: '', newStatus: '', label: '' })
    }
  }

  const handleApprove = async (solicitudId: string) => {
    setPendingActionId(solicitudId)
    try {
      await approveRequest(solicitudId)
      setSuccessMessage('Solicitud aprobada correctamente')
      invalidateAll()
    } catch {
      // error handled by UI
    } finally {
      setPendingActionId(null)
    }
  }

  const pendingCount = requests.length

  return (
    <Page
      title="Usuarios"
      subtitle="Gestión de cuentas y solicitudes de acceso"
      actions={
        <Button icon={<Plus size={14} />} onClick={() => setUserModal({ open: true, editing: null })}>
          Nuevo usuario
        </Button>
      }
    >
      {successMessage && (
        <div className="mb-4">
          <Alert variant="success" onClose={() => setSuccessMessage(null)}>{successMessage}</Alert>
        </div>
      )}

      {/* Tabs */}
      <div className="flex items-center gap-1 border-b border-blue-100 mb-4">
        {([
          { key: 'users' as ActiveTab, label: 'Usuarios', icon: <Users size={14} /> },
          {
            key: 'requests' as ActiveTab,
            label: 'Solicitudes',
            icon: <UserCheck size={14} />,
            badge: pendingCount,
          },
        ]).map(({ key, label, icon, badge }) => (
          <button
            key={key}
            onClick={() => setActiveTab(key)}
            className={`flex items-center gap-2 px-4 py-2.5 text-sm font-medium border-b-2 -mb-px transition-colors ${
              activeTab === key
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-slate-400 hover:text-slate-600'
            }`}
          >
            {icon}
            {label}
            {badge ? (
              <span className="bg-warn text-white text-xs font-bold px-1.5 py-0.5 rounded-full min-w-[1.25rem] text-center">
                {badge}
              </span>
            ) : null}
          </button>
        ))}
      </div>

      {activeTab === 'users' && (
        <Card noPadding>
          {/* Filters */}
          <div className="flex flex-wrap items-center gap-3 p-4 border-b border-blue-50">
            <Input
              placeholder="Buscar por nombre, correo o usuario…"
              leadingIcon={<Search size={13} />}
              className="max-w-xs h-8 text-xs"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <Select
              options={STATUS_OPTIONS}
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="w-44 h-8 text-xs"
            />
            <Select
              options={ROLE_OPTIONS}
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="w-44 h-8 text-xs"
            />
            <Badge variant="default" className="ml-auto">
              {filteredUsers.length} usuario{filteredUsers.length !== 1 ? 's' : ''}
            </Badge>
          </div>

          <Table>
            <Table.Head>
              <tr>
                <Table.Th>Usuario</Table.Th>
                <Table.Th>Identificación</Table.Th>
                <Table.Th>Correo</Table.Th>
                <Table.Th>Rol</Table.Th>
                <Table.Th>Último acceso</Table.Th>
                <Table.Th>Estado</Table.Th>
                <Table.Th />
              </tr>
            </Table.Head>
            <Table.Body>
              {loadingUsers ? (
                <Table.Empty icon={<RefreshCw size={28} className="animate-spin" />} title="Cargando usuarios…" />
              ) : usersError ? (
                <Table.Empty icon={<Users size={36} />} title="Error al cargar usuarios" description="Verifica tu conexión" />
              ) : filteredUsers.length === 0 ? (
                <Table.Empty
                  icon={<Users size={36} />}
                  title="Sin usuarios"
                  description="No se encontraron usuarios con los filtros aplicados"
                  action={
                    <Button size="sm" icon={<Plus size={13} />} onClick={() => setUserModal({ open: true, editing: null })}>
                      Nuevo usuario
                    </Button>
                  }
                />
              ) : (
                filteredUsers.map((user) => (
                  <Table.Row key={user.id}>
                    <Table.Td>
                      <div className="flex items-center gap-2.5">
                        <div className="w-7 h-7 rounded-lg bg-blue-100 flex items-center justify-center text-xs font-bold text-blue-600 shrink-0">
                          {user.firstName[0]}{user.lastName[0]}
                        </div>
                        <div>
                          <p className="font-medium text-slate-800">{user.firstName} {user.lastName}</p>
                          <p className="text-xs text-slate-400 font-mono">@{user.username}</p>
                        </div>
                      </div>
                    </Table.Td>
                    <Table.Td>
                      <span className="font-mono text-xs text-slate-500">
                        {user.identificationType} {user.identificationNumber}
                      </span>
                    </Table.Td>
                    <Table.Td className="text-slate-500 text-xs">{user.email}</Table.Td>
                    <Table.Td>
                      <Badge variant={user.role === 'ROLE_OWNER' ? 'blue' : 'default'}>
                        {ROLE_LABELS[user.role] ?? user.role}
                      </Badge>
                    </Table.Td>
                    <Table.Td className="text-slate-400 text-xs">
                      {user.lastAccess ? formatDate(user.lastAccess) : '—'}
                    </Table.Td>
                    <Table.Td>
                      <UserStatusBadge status={user.status as UserStatus} />
                    </Table.Td>
                    <Table.Td>
                      <div className="flex items-center gap-1 justify-end">
                        <button
                          title="Editar"
                          onClick={() => setUserModal({ open: true, editing: user })}
                          className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                        >
                          <MoreHorizontal size={14} />
                        </button>
                        <button
                          title={user.status === 'ACTIVE' ? 'Desactivar' : 'Activar'}
                          onClick={() =>
                            setStatusConfirm({
                              open: true,
                              userId: user.id,
                              newStatus: user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE',
                              label: user.status === 'ACTIVE' ? 'Desactivado' : 'Activado',
                            })
                          }
                          className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                        >
                          {user.status === 'ACTIVE' ? <UserX size={14} /> : <UserCheck size={14} />}
                        </button>
                        <button
                          title="Restablecer contraseña"
                          onClick={() => setResetModal({ open: true, userId: user.id })}
                          className="p-1.5 rounded-lg text-slate-300 hover:text-blue-500 hover:bg-blue-50 transition-all"
                        >
                          <KeyRound size={14} />
                        </button>
                      </div>
                    </Table.Td>
                  </Table.Row>
                ))
              )}
            </Table.Body>
          </Table>
        </Card>
      )}

      {activeTab === 'requests' && (
        <Card noPadding>
          <Table>
            <Table.Head>
              <tr>
                <Table.Th>Solicitante</Table.Th>
                <Table.Th>Correo</Table.Th>
                <Table.Th>Usuario</Table.Th>
                <Table.Th>Rol solicitado</Table.Th>
                <Table.Th>Fecha</Table.Th>
                <Table.Th />
              </tr>
            </Table.Head>
            <Table.Body>
              {loadingRequests ? (
                <Table.Empty icon={<RefreshCw size={28} className="animate-spin" />} title="Cargando solicitudes…" />
              ) : requests.length === 0 ? (
                <Table.Empty
                  icon={<UserCheck size={36} />}
                  title="Sin solicitudes pendientes"
                  description="Todas las solicitudes han sido procesadas"
                />
              ) : (
                requests.map((req) => (
                  <Table.Row key={req.solicitudId}>
                    <Table.Td>
                      <p className="font-medium text-slate-800">{req.firstName} {req.lastName}</p>
                    </Table.Td>
                    <Table.Td className="text-slate-500 text-xs">{req.email}</Table.Td>
                    <Table.Td>
                      <span className="font-mono text-xs text-slate-500">@{req.username}</span>
                    </Table.Td>
                    <Table.Td>
                      <Badge variant="default">{ROLE_LABELS[req.rolSolicitado] ?? req.rolSolicitado}</Badge>
                    </Table.Td>
                    <Table.Td className="text-slate-400 text-xs">
                      {req.fechaCreacion ? formatDate(req.fechaCreacion) : '—'}
                    </Table.Td>
                    <Table.Td>
                      <div className="flex items-center gap-2 justify-end">
                        <Button
                          size="sm"
                          variant="outline"
                          icon={<ShieldCheck size={13} />}
                          loading={pendingActionId === req.solicitudId}
                          onClick={() => handleApprove(req.solicitudId)}
                        >
                          Aprobar
                        </Button>
                        <Button
                          size="sm"
                          variant="danger"
                          icon={<ShieldX size={13} />}
                          onClick={() => setRejectModal({ open: true, solicitudId: req.solicitudId })}
                        >
                          Rechazar
                        </Button>
                      </div>
                    </Table.Td>
                  </Table.Row>
                ))
              )}
            </Table.Body>
          </Table>
        </Card>
      )}

      {/* Modals */}
      <UserFormModal
        key={userModal.editing?.id ?? 'new'}
        open={userModal.open}
        onClose={() => setUserModal({ open: false, editing: null })}
        editing={userModal.editing}
        onSuccess={() => {
          setSuccessMessage(userModal.editing ? 'Usuario actualizado' : 'Usuario creado correctamente')
          invalidateAll()
        }}
      />

      <ResetPasswordModal
        open={resetModal.open}
        onClose={() => setResetModal({ open: false, userId: '' })}
        userId={resetModal.userId}
        onSuccess={() => setSuccessMessage('Contraseña restablecida correctamente')}
      />

      <ConfirmModal
        open={statusConfirm.open}
        onClose={() => setStatusConfirm({ open: false, userId: '', newStatus: '', label: '' })}
        onConfirm={handleChangeStatus}
        title={`${statusConfirm.newStatus === 'ACTIVE' ? 'Activar' : 'Desactivar'} usuario`}
        message={`¿Estás seguro de que deseas ${statusConfirm.newStatus === 'ACTIVE' ? 'activar' : 'desactivar'} este usuario?${statusConfirm.newStatus === 'INACTIVE' ? ' Sus sesiones activas serán invalidadas.' : ''}`}
        confirmLabel={statusConfirm.newStatus === 'ACTIVE' ? 'Activar' : 'Desactivar'}
        confirmVariant={statusConfirm.newStatus === 'ACTIVE' ? 'primary' : 'danger'}
        loading={pendingActionId === statusConfirm.userId}
      />

      <RejectRequestModal
        open={rejectModal.open}
        onClose={() => setRejectModal({ open: false, solicitudId: '' })}
        solicitudId={rejectModal.solicitudId}
        onSuccess={() => {
          setSuccessMessage('Solicitud rechazada')
          invalidateAll()
        }}
      />
    </Page>
  )
}
