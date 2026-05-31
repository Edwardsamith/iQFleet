# iQFleet — Estándares de Desarrollo Frontend

> Documento normativo. Todo el código nuevo debe cumplir estas pautas antes de ser integrado.

---

## 1. Stack tecnológico

| Herramienta | Versión | Propósito |
|---|---|---|
| **Vite** | 5.x | Build tool y dev server |
| **React** | 18.x | UI framework |
| **TypeScript** | 5.x (strict) | Tipado estático |
| **Tailwind CSS** | 3.x | Estilos utilitarios |
| **React Router** | 6.x | Enrutamiento |
| **TanStack Query** | 5.x | Caché y estado servidor |
| **Axios** | 1.x | Cliente HTTP |
| **React Hook Form + Zod** | — | Formularios y validación |
| **Lucide React** | — | Iconografía |

---

## 2. Estructura de carpetas

```
src/
├── components/
│   ├── ui/           # Primitivos del design system (Button, Input, Card…)
│   └── layout/       # Estructura de la aplicación (Sidebar, Header, AppLayout)
├── pages/            # Una página = un archivo = una ruta
├── hooks/            # Custom hooks reutilizables
├── services/         # Funciones de llamada a API (una por entidad)
├── types/            # Tipos TypeScript del dominio
├── lib/              # Utilidades puras (http.ts, utils.ts)
└── styles/           # globals.css (solo aquí se importa @tailwind)
```

### Reglas de organización
- **Páginas** → `src/pages/<Entidad>Page.tsx` (ej. `DriversPage.tsx`)
- **Componentes UI** → siempre en `src/components/ui/`, exportados desde `index.ts`
- **Componentes de negocio** → subdirectorio propio: `src/components/drivers/DriverCard.tsx`
- No crear `index.ts` barrel en páginas; importar directamente.

---

## 3. Design Tokens (paleta)

Todos los colores se definen en `tailwind.config.ts`. **Nunca usar valores hex directos en clases.**

### Colores de marca
```
brand-700   → color principal de acciones
brand-800   → hover de acciones primarias
brand-900   → fondo del sidebar
brand-50    → fondos de elementos brand light
```

### Colores de superficie
```
surface-secondary  → fondo global de la app (#F8FAFC)
surface            → fondo de cards y paneles (#FFF)
surface-tertiary   → fondo de filas alternas, inputs deshabilitados
```

### Colores semánticos de estado
```
status-valid        → verde (VALID, ACTIVE, INCOME)
status-valid-bg     → fondo verde claro
status-warning      → ámbar (EXPIRING_SOON, UNDER_MAINTENANCE)
status-warning-bg   → fondo ámbar claro
status-danger       → rojo (EXPIRED, BLOCKED, EXPENSE)
status-danger-bg    → fondo rojo claro
status-inactive     → gris (INACTIVE, CANCELLED)
status-info         → cian (info, categorías)
```

### Tipografía
```
font-sans  → Inter (UI principal)
font-mono  → JetBrains Mono (códigos, placas, referencias)
```

---

## 4. Componentes UI (Design System)

### 4.1 Button

```tsx
import { Button } from '@/components/ui'

// Variantes disponibles
<Button variant="primary">Guardar</Button>
<Button variant="secondary">Cancelar</Button>
<Button variant="ghost">Ver detalle</Button>
<Button variant="danger">Eliminar</Button>
<Button variant="outline">Exportar</Button>

// Tamaños
<Button size="sm" />   // h-8, text-xs
<Button size="md" />   // h-9, text-sm  ← default
<Button size="lg" />   // h-11, text-base

// Con ícono
<Button icon={<Plus size={15} />}>Nuevo</Button>
<Button icon={<LogIn size={15} />} iconPosition="right">Ingresar</Button>

// Estado de carga
<Button loading={isSaving}>Guardando…</Button>
```

**Regla:** El texto de los botones primarios de CTA siempre va en español. No usar "Submit" ni "Save".

### 4.2 Input / Select / Textarea

```tsx
import { Input, Select, Textarea } from '@/components/ui'

<Input
  label="Nombre"
  name="firstName"
  required
  error={errors.firstName?.message}
  hint="Tal como aparece en la cédula"
/>

<Select
  label="Categoría"
  options={[{ value: 'B3', label: 'B3 — Bus' }]}
  placeholder="Selecciona una categoría"
/>

<Textarea label="Notas" hint="Opcional" />
```

**Regla:** Siempre proveer `label`. Los campos requeridos deben tener `required` para mostrar el asterisco visual. Los errores se pasan como string a `error`.

### 4.3 Card

```tsx
import { Card, StatCard } from '@/components/ui'

// Card básica
<Card>
  <Card.Header title="Vehículos" subtitle="Resumen de flota" action={<Button>Ver todos</Button>} />
  {/* contenido */}
</Card>

// Card sin padding (para tablas)
<Card noPadding>
  <Table>…</Table>
</Card>

// Stat card para dashboards
<StatCard
  label="Documentos vencidos"
  value={7}
  icon={<AlertTriangle size={20} />}
  variant="danger"
/>
```

### 4.4 Badge

```tsx
import {
  Badge,
  DocumentStatusBadge,
  DriverStatusBadge,
  VehicleStatusBadge,
  MovementTypeBadge,
} from '@/components/ui'

// Badge genérico
<Badge variant="info">B3</Badge>
<Badge variant="warning" dot>Por vencer</Badge>

// Badges de dominio (usar siempre estos para entidades conocidas)
<DocumentStatusBadge status={doc.status} />
<DriverStatusBadge status={driver.status} />
<VehicleStatusBadge status={vehicle.status} />
<MovementTypeBadge type={movement.movementType} />
```

**Regla:** Para entidades del dominio usar siempre los badges específicos, no el genérico. Garantiza consistencia de color y label.

### 4.5 Table

```tsx
import { Table } from '@/components/ui'

<Table>
  <Table.Head>
    <tr>
      <Table.Th>Nombre</Table.Th>
      <Table.Th>Estado</Table.Th>
    </tr>
  </Table.Head>
  <Table.Body>
    {isLoading ? (
      <Table.Skeleton cols={2} rows={5} />
    ) : data.length === 0 ? (
      <Table.Empty title="Sin registros" description="..." />
    ) : (
      data.map(row => (
        <Table.Row key={row.id} onClick={() => navigate(`/${row.id}`)}>
          <Table.Td>{row.name}</Table.Td>
          <Table.Td><DriverStatusBadge status={row.status} /></Table.Td>
        </Table.Row>
      ))
    )}
  </Table.Body>
</Table>
```

**Regla:** Toda tabla debe manejar los tres estados: loading (usar `Table.Skeleton`), vacío (`Table.Empty`) y datos.

### 4.6 Alert / Modal

```tsx
import { Alert, Modal, ConfirmModal } from '@/components/ui'

// Alerta inline
<Alert variant="warning" title="Atención" onClose={() => setOpen(false)}>
  Este documento vence en 5 días.
</Alert>

// Modal
<Modal open={isOpen} onClose={() => setOpen(false)} title="Nuevo conductor" size="lg"
  footer={
    <>
      <Button variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
      <Button onClick={handleSave} loading={isSaving}>Guardar</Button>
    </>
  }
>
  {/* formulario */}
</Modal>

// Confirmación destructiva
<ConfirmModal
  open={isOpen}
  onClose={() => setOpen(false)}
  onConfirm={handleDelete}
  title="Inactivar conductor"
  message="¿Estás seguro? Esta acción desactivará al conductor."
  confirmLabel="Inactivar"
  confirmVariant="danger"
/>
```

---

## 5. Convenciones de código

### Nombrado
| Elemento | Convención | Ejemplo |
|---|---|---|
| Componentes | PascalCase | `DriverCard`, `DocumentStatusBadge` |
| Hooks | camelCase con `use` | `useDrivers`, `useDocumentAlerts` |
| Servicios | camelCase con entidad | `driversService`, `documentsService` |
| Tipos/interfaces | PascalCase | `Driver`, `CreateDocumentCommand` |
| Archivos de componentes | PascalCase.tsx | `Button.tsx` |
| Archivos de hooks/utils | camelCase.ts | `useDrivers.ts`, `utils.ts` |
| Páginas | PascalCase + Page | `DriversPage.tsx` |

### Componentes funcionales
```tsx
// ✅ Correcto — arrow function con tipo explícito de props
interface DriverCardProps {
  driver: Driver
  onEdit?: () => void
}

export function DriverCard({ driver, onEdit }: DriverCardProps) {
  return (…)
}

// ❌ Evitar — default export sin nombre, React.FC
export default (props: any) => (…)
```

### Importaciones — orden obligatorio
```tsx
// 1. React / hooks de React
import { useState, useEffect } from 'react'

// 2. Librerías externas (router, query, etc.)
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'

// 3. Componentes internos
import { Button, Card } from '@/components/ui'
import { Page } from '@/components/layout'

// 4. Hooks propios
import { useDrivers } from '@/hooks/useDrivers'

// 5. Servicios y utilidades
import { driversService } from '@/services/driversService'
import { formatDate } from '@/lib/utils'

// 6. Tipos
import type { Driver } from '@/types'
```

---

## 6. Data fetching con TanStack Query

```tsx
// services/driversService.ts
import { http } from '@/lib/http'
import type { Driver } from '@/types'

export const driversService = {
  getAll: (params?: Record<string, string>) =>
    http.get<Driver[]>('/drivers', { params }).then(r => r.data),

  getById: (id: string) =>
    http.get<Driver>(`/drivers/${id}`).then(r => r.data),

  create: (body: CreateDriverDto) =>
    http.post<void>('/drivers', body),

  updateStatus: (id: string, status: DriverStatus) =>
    http.patch<void>(`/drivers/${id}/status`, { status }),
}

// hooks/useDrivers.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { driversService } from '@/services/driversService'

export function useDrivers() {
  return useQuery({
    queryKey: ['drivers'],
    queryFn: () => driversService.getAll(),
  })
}

export function useCreateDriver() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: driversService.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['drivers'] }),
  })
}
```

**Reglas:**
- `queryKey` siempre como array: `['drivers']`, `['drivers', id]`, `['drivers', filters]`
- Servicios devuelven el dato directamente (`.then(r => r.data)`)
- `useMutation` siempre invalida el queryKey padre en `onSuccess`
- No usar `useEffect` + `fetch` para datos del servidor; usar siempre Query

---

## 7. Formularios con React Hook Form + Zod

```tsx
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'

const schema = z.object({
  firstName: z.string().min(2, 'Mínimo 2 caracteres'),
  licenseCategory: z.enum(['A1','A2','B1','B2','B3','C1','C2','C3']),
  licenseExpiry: z.string().min(1, 'Fecha requerida'),
})

type FormData = z.infer<typeof schema>

function CreateDriverForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  })

  const onSubmit = (data: FormData) => { /* llamar servicio */ }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Input label="Nombre" error={errors.firstName?.message} {...register('firstName')} />
      <Button type="submit">Guardar</Button>
    </form>
  )
}
```

**Regla:** Todo formulario de creación/edición debe usar Zod schema. Los mensajes de error van en español.

---

## 8. Enrutamiento

```
/login           → LoginPage (sin layout)
/dashboard       → DashboardPage
/drivers         → DriversPage (lista)
/drivers/:id     → DriverDetailPage (detalle)
/vehicles        → VehiclesPage
/vehicles/:id    → VehicleDetailPage
/documents       → DocumentsPage
/finances        → FinancesPage
/settings        → SettingsPage
```

- Las rutas de detalle van en la misma carpeta de páginas: `DriverDetailPage.tsx`
- No usar rutas anidadas para modales; usar estado local + componente `Modal`

---

## 9. Accesibilidad mínima requerida

- Todo botón sin texto visible debe tener `aria-label`
- Imágenes decorativas → `alt=""`; imágenes informativas → alt descriptivo
- Tablas de datos → `<caption>` o `aria-label` en `<table>`
- Los modales usan `<dialog>` nativo (ya implementado en `Modal.tsx`)
- El foco debe quedar atrapado dentro del modal mientras está abierto (el dialog nativo lo hace)
- El sistema de colores cumple contraste WCAG AA mínimo

---

## 10. Guía rápida para agregar una nueva sección

1. Crear `src/pages/<Entidad>Page.tsx` con `<Page title="…">`
2. Crear `src/services/<entidad>Service.ts` con las llamadas a la API
3. Crear `src/hooks/use<Entidad>.ts` con los hooks de Query
4. Añadir la ruta en `App.tsx`
5. Añadir el ítem en `navItems` de `Sidebar.tsx`
6. Si hay componentes de negocio complejos → `src/components/<entidad>/`

---

## 11. Lo que NO hacer

| Evitar | Usar en cambio |
|---|---|
| `useEffect` + `fetch` para datos | TanStack Query |
| Colores hex directos (`#1E40AF`) | Tokens de Tailwind (`text-brand-700`) |
| `any` en TypeScript | Tipos del dominio o `unknown` |
| Comentarios que explican qué hace el código | Nombres descriptivos |
| `console.log` en producción | Eliminar o usar logger |
| Traducción de strings al inglés en UI | Todo el UI en español (Colombia) |
| `export default` anónimo | `export function NombreClaro` |
| Lógica de negocio en componentes de UI | Hooks o servicios |
