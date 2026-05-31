import { Plus, Truck } from 'lucide-react'
import { Page } from '@/components/layout'
import { Button, Card, Table, VehicleStatusBadge, Badge } from '@/components/ui'
import type { Vehicle } from '@/types'

const mockVehicles: Vehicle[] = [
  {
    id: '1', createdAt: '2025-01-01T00:00:00Z', updatedAt: '2025-01-01T00:00:00Z',
    plateNumber: 'ABC-123', brand: 'Chevrolet', vehicleModel: 'NPR',
    vehicleType: 'BUS', year: 2020, status: 'ACTIVE', registrationDate: '2025-01-01',
  },
  {
    id: '2', createdAt: '2025-01-15T00:00:00Z', updatedAt: '2025-01-15T00:00:00Z',
    plateNumber: 'XYZ-456', brand: 'Mercedes', vehicleModel: 'Sprinter',
    vehicleType: 'MICROBUS', year: 2019, status: 'UNDER_MAINTENANCE', registrationDate: '2025-01-15',
  },
]

const typeLabel: Record<string, string> = {
  BUS: 'Bus', TAXI: 'Taxi', MINIBUS: 'Minibús', MICROBUS: 'Microbús', VAN: 'Van',
}

export function VehiclesPage() {
  return (
    <Page
      title="Vehículos"
      subtitle="Inventario y estado de la flota"
      actions={<Button icon={<Plus size={14} />}>Nuevo vehículo</Button>}
    >
      <Card noPadding>
        <div className="flex items-center gap-3 p-4 border-b border-blue-50">
          <Badge variant="default" className="ml-auto">{mockVehicles.length} vehículos</Badge>
        </div>

        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Placa</Table.Th>
              <Table.Th>Marca / Modelo</Table.Th>
              <Table.Th>Tipo</Table.Th>
              <Table.Th>Año</Table.Th>
              <Table.Th>Conductor</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {mockVehicles.length === 0 ? (
              <Table.Empty
                icon={<Truck size={36} />}
                title="Sin vehículos registrados"
                action={<Button size="sm" icon={<Plus size={13} />}>Nuevo vehículo</Button>}
              />
            ) : (
              mockVehicles.map((v) => (
                <Table.Row key={v.id}>
                  <Table.Td>
                    <span className="font-mono font-semibold text-slate-800">{v.plateNumber}</span>
                  </Table.Td>
                  <Table.Td>
                    <p className="font-medium text-slate-800">{v.brand}</p>
                    <p className="text-xs text-slate-400 mt-0.5">{v.vehicleModel}</p>
                  </Table.Td>
                  <Table.Td><Badge variant="default">{typeLabel[v.vehicleType]}</Badge></Table.Td>
                  <Table.Td className="text-slate-500">{v.year}</Table.Td>
                  <Table.Td>
                    {v.assignedDriverId
                      ? <Badge variant="success" dot>Asignado</Badge>
                      : <Badge variant="inactive">Sin conductor</Badge>}
                  </Table.Td>
                  <Table.Td><VehicleStatusBadge status={v.status} /></Table.Td>
                  <Table.Td><Button variant="ghost" size="sm">Ver</Button></Table.Td>
                </Table.Row>
              ))
            )}
          </Table.Body>
        </Table>
      </Card>
    </Page>
  )
}
