import { Plus, FileText } from 'lucide-react'
import { Page } from '@/components/layout'
import { Button, Card, Table, DocumentStatusBadge, Badge } from '@/components/ui'
import { formatDate } from '@/lib/utils'
import type { Document } from '@/types'

const mockDocuments: Document[] = [
  {
    id: '1', createdAt: '2025-01-01T00:00:00Z', updatedAt: '2025-01-01T00:00:00Z',
    name: 'SOAT Bus ABC-123', documentType: 'SOAT', referenceNumber: 'S-20250101',
    issuingEntity: 'Seguros Bolívar', issueDate: '2025-01-01', expiryDate: '2025-06-04',
    status: 'EXPIRING_SOON', vehicleId: '1', uploadedBy: 'admin', uploadDate: '2025-01-01T00:00:00Z',
  },
  {
    id: '2', createdAt: '2025-01-15T00:00:00Z', updatedAt: '2025-01-15T00:00:00Z',
    name: 'Licencia J. Pérez', documentType: 'DRIVING_LICENSE', referenceNumber: 'L-B3-001234',
    issueDate: '2023-09-15', expiryDate: '2026-09-15',
    status: 'VALID', driverId: '1', uploadedBy: 'admin', uploadDate: '2025-01-15T00:00:00Z',
  },
]

const docTypeLabel: Record<string, string> = {
  SOAT: 'SOAT', RTM: 'Rev. técnica', OPERATION_CARD: 'Tarjeta operación',
  PROPERTY_CARD: 'Tarjeta propiedad', LIABILITY_POLICY: 'Póliza RC',
  DRIVING_LICENSE: 'Licencia', MEDICAL_CERTIFICATE: 'Cert. médico',
  EMPLOYMENT_CONTRACT: 'Contrato', TRAFFIC_CLEARANCE: 'Paz y salvo',
  COMPANY_LICENSE: 'Habilitación', ROUTE_CONTRACT: 'Contrato ruta',
  OTHER_DRIVER: 'Otro', OTHER_VEHICLE: 'Otro', OTHER_ADMIN: 'Otro',
}

export function DocumentsPage() {
  return (
    <Page
      title="Documentos"
      subtitle="Gestión documental y alertas de vencimiento"
      actions={<Button icon={<Plus size={14} />}>Nuevo documento</Button>}
    >
      <Card noPadding>
        <div className="flex items-center gap-3 p-4 border-b border-blue-50">
          <Badge variant="default" className="ml-auto">{mockDocuments.length} documentos</Badge>
        </div>

        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Documento</Table.Th>
              <Table.Th>Tipo</Table.Th>
              <Table.Th>Emisor</Table.Th>
              <Table.Th>Emisión</Table.Th>
              <Table.Th>Vencimiento</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {mockDocuments.length === 0 ? (
              <Table.Empty
                icon={<FileText size={36} />}
                title="Sin documentos registrados"
                action={<Button size="sm" icon={<Plus size={13} />}>Nuevo documento</Button>}
              />
            ) : (
              mockDocuments.map((doc) => (
                <Table.Row key={doc.id}>
                  <Table.Td>
                    <p className="font-medium text-slate-800">{doc.name}</p>
                    {doc.referenceNumber && (
                      <p className="text-xs font-mono text-slate-400 mt-0.5">{doc.referenceNumber}</p>
                    )}
                  </Table.Td>
                  <Table.Td><Badge variant="default">{docTypeLabel[doc.documentType]}</Badge></Table.Td>
                  <Table.Td className="text-slate-500">{doc.issuingEntity ?? '—'}</Table.Td>
                  <Table.Td className="text-slate-500">{formatDate(doc.issueDate)}</Table.Td>
                  <Table.Td className="text-slate-500">{formatDate(doc.expiryDate)}</Table.Td>
                  <Table.Td><DocumentStatusBadge status={doc.status} /></Table.Td>
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
