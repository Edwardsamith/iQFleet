import { Plus, DollarSign, TrendingUp, TrendingDown } from 'lucide-react'
import { Page } from '@/components/layout'
import { Button, Card, Table, MovementTypeBadge, MovementStatusBadge, StatCard } from '@/components/ui'
import { formatCurrency, formatDate } from '@/lib/utils'
import type { FinancialMovement } from '@/types'

const mockMovements: FinancialMovement[] = [
  {
    id: '1', createdAt: '2025-05-01T00:00:00Z', updatedAt: '2025-05-01T00:00:00Z',
    movementType: 'INCOME', category: 'DAILY_COLLECTION', paymentMethod: 'CASH',
    amount: 850000, date: '2025-05-01', description: 'Recaudo diario bus ABC-123',
    status: 'ACTIVE', registeredBy: 'admin',
  },
  {
    id: '2', createdAt: '2025-05-02T00:00:00Z', updatedAt: '2025-05-02T00:00:00Z',
    movementType: 'EXPENSE', category: 'FUEL', paymentMethod: 'CASH',
    amount: 120000, date: '2025-05-02', description: 'Combustible bus XYZ-456',
    status: 'ACTIVE', registeredBy: 'admin',
  },
]

const categoryLabel: Record<string, string> = {
  DAILY_COLLECTION: 'Recaudo diario', SUBSIDY: 'Subsidio', OTHER_INCOME: 'Otro ingreso',
  FUEL: 'Combustible', PREVENTIVE_MAINTENANCE: 'Mant. preventivo',
  CORRECTIVE_MAINTENANCE: 'Mant. correctivo', SALARY: 'Salario',
  INSURANCE: 'Seguro', PAPERWORK: 'Trámites', TAX: 'Impuesto', OTHER_EXPENSE: 'Otro egreso',
}

export function FinancesPage() {
  const totalIncome = mockMovements.filter(m => m.movementType === 'INCOME').reduce((s, m) => s + m.amount, 0)
  const totalExpense = mockMovements.filter(m => m.movementType === 'EXPENSE').reduce((s, m) => s + m.amount, 0)

  return (
    <Page
      title="Finanzas"
      subtitle="Movimientos y balance de la flota"
      actions={<Button icon={<Plus size={14} />}>Registrar movimiento</Button>}
    >
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <StatCard label="Ingresos del mes"  value={formatCurrency(totalIncome)}           variant="success" icon={<TrendingUp size={18} />} />
        <StatCard label="Egresos del mes"   value={formatCurrency(totalExpense)}           variant="danger"  icon={<TrendingDown size={18} />} />
        <StatCard label="Balance neto"       value={formatCurrency(totalIncome - totalExpense)} variant="default" icon={<DollarSign size={18} />} />
      </div>

      <Card noPadding>
        <Table>
          <Table.Head>
            <tr>
              <Table.Th>Fecha</Table.Th>
              <Table.Th>Descripción</Table.Th>
              <Table.Th>Categoría</Table.Th>
              <Table.Th>Tipo</Table.Th>
              <Table.Th>Monto</Table.Th>
              <Table.Th>Estado</Table.Th>
              <Table.Th />
            </tr>
          </Table.Head>
          <Table.Body>
            {mockMovements.map((m) => (
              <Table.Row key={m.id}>
                <Table.Td className="text-slate-500">{formatDate(m.date)}</Table.Td>
                <Table.Td>
                  <p className="font-medium text-slate-800">{m.description}</p>
                </Table.Td>
                <Table.Td className="text-slate-500">{categoryLabel[m.category]}</Table.Td>
                <Table.Td><MovementTypeBadge type={m.movementType} /></Table.Td>
                <Table.Td>
                  <span className={`font-semibold tabular-nums ${m.movementType === 'INCOME' ? 'text-ok' : 'text-err'}`}>
                    {m.movementType === 'EXPENSE' ? '−' : '+'}{formatCurrency(m.amount)}
                  </span>
                </Table.Td>
                <Table.Td><MovementStatusBadge status={m.status} /></Table.Td>
                <Table.Td><Button variant="ghost" size="sm">Ver</Button></Table.Td>
              </Table.Row>
            ))}
          </Table.Body>
        </Table>
      </Card>
    </Page>
  )
}
