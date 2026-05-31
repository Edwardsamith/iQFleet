import { Search, Plus, UserCircle } from 'lucide-react'
import { Page } from '@/components/layout'
import { Button, Card, Table, DriverStatusBadge, Input, Badge } from '@/components/ui'
import { formatDate } from '@/lib/utils'
import type { Driver } from '@/types'

const mockDrivers: Driver[] = [
  {
    id: '1', createdAt: '2025-01-10T00:00:00Z', updatedAt: '2025-01-10T00:00:00Z',
    identificationType: 'CC', identificationNumber: '10234567',
    firstName: 'Juan', lastName: 'Pérez',
    licenseNumber: 'B3-001234', licenseCategory: 'B3', licenseExpiry: '2025-09-15',
    phone: '3001234567', email: 'juan.perez@example.com',
    status: 'ACTIVE', registrationDate: '2025-01-10',
  },
  {
    id: '2', createdAt: '2025-02-05T00:00:00Z', updatedAt: '2025-02-05T00:00:00Z',
    identificationType: 'CC', identificationNumber: '20456789',
    firstName: 'María', lastName: 'González',
    licenseNumber: 'C2-005678', licenseCategory: 'C2', licenseExpiry: '2026-03-20',
    phone: '3119876543',
    status: 'ACTIVE', registrationDate: '2025-02-05',
  },
]

export function DriversPage() {
  return (
    <Page
      title="Conductores"
      subtitle="Conductores registrados en el sistema"
      actions={<Button icon={<Plus size={14} />}>Nuevo conductor</Button>}
    >
      <Card noPadding>
        <div className="flex items-center gap-3 p-4 border-b border-blue-50">
          <Input
            placeholder="Buscar por nombre o identificación…"
            leadingIcon={<Search size={13} />}
            className="max-w-xs h-8 text-xs"
          />
          <Badge variant="default" className="ml-auto">
            {mockDrivers.length} conductores
          </Badge>
        </div>

        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Conductor</Table.Th>
              <Table.Th>Identificación</Table.Th>
              <Table.Th>Categoría</Table.Th>
              <Table.Th>Venc. licencia</Table.Th>
              <Table.Th>Teléfono</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {mockDrivers.length === 0 ? (
              <Table.Empty
                icon={<UserCircle size={36} />}
                title="Sin conductores registrados"
                description="Agrega el primer conductor para comenzar"
                action={<Button size="sm" icon={<Plus size={13} />}>Nuevo conductor</Button>}
              />
            ) : (
              mockDrivers.map((d) => (
                <Table.Row key={d.id}>
                  <Table.Td>
                    <p className="font-medium text-slate-800">{d.firstName} {d.lastName}</p>
                    {d.email && <p className="text-xs text-slate-400 mt-0.5">{d.email}</p>}
                  </Table.Td>
                  <Table.Td>
                    <span className="font-mono text-xs text-slate-500">{d.identificationType} {d.identificationNumber}</span>
                  </Table.Td>
                  <Table.Td>
                    <Badge variant="default">{d.licenseCategory}</Badge>
                  </Table.Td>
                  <Table.Td className="text-slate-500">{formatDate(d.licenseExpiry)}</Table.Td>
                  <Table.Td className="text-slate-500">{d.phone}</Table.Td>
                  <Table.Td><DriverStatusBadge status={d.status} /></Table.Td>
                  <Table.Td>
                    <Button variant="ghost" size="sm">Ver</Button>
                  </Table.Td>
                </Table.Row>
              ))
            )}
          </Table.Body>
        </Table>
      </Card>
    </Page>
  )
}
