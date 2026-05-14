# RF-004 — Gestión Financiera

| Campo | Detalle |
|-------|---------|
| **ID** | RF-004 |
| **Módulo** | Gestión Financiera |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-001 (conductores), RF-002 (vehículos), RF-005 (autenticación) |
| **Relacionado con** | RF-009 (vista de métricas financieras), RF-010 (reportes) |

---

## 1. Descripción

El sistema debe permitir administrar la información financiera relacionada con la operación de la flota. Esto incluye el registro de ingresos y egresos por vehículo o conductor, la clasificación por categorías, el cálculo automático de balances y la consulta de históricos con filtros. Este módulo responde directamente a la problemática central del proyecto: propietarios que no tienen claridad sobre los registros administrativos necesarios para tomar decisiones de negocio.

---

## 2. Actores

| Actor | Permisos |
|-------|----------|
| Propietario (`ROLE_OWNER`) | Acceso completo: registrar, consultar, filtrar, ver balances |
| Administrador (`ROLE_ADMIN`) | Registrar movimientos y consultar históricos |
| Sistema (iQFleet) | Calcula y actualiza balances automáticamente |

---

## 3. Criterios de Aceptación

### CA-004-1: Acceso al módulo
1. El sistema debe ofrecer un módulo de gestión financiera para usuarios autorizados.

### CA-004-2: Registrar movimiento financiero
2. El sistema debe permitir registrar ingresos y egresos asociados a:
   - Vehículos
   - Conductores
   - Procesos operativos
3. El sistema debe permitir registrar la siguiente información de cada movimiento financiero:
   - Tipo de movimiento (Ingreso / Egreso)
   - Categoría
   - Valor
   - Fecha del movimiento
   - Método de pago
   - Responsable del registro
   - Vehículo asociado
   - Observaciones
4. El sistema debe permitir clasificar los movimientos financieros según categorías.
5. El sistema debe permitir registrar el tipo de transacción financiera:
   - Efectivo
   - Transferencia
6. El sistema debe validar que los valores registrados correspondan correctamente al tipo de movimiento seleccionado (Ingreso / Egreso).

### CA-004-3: Cálculo de balances
7. El sistema debe calcular automáticamente los balances financieros generales basados en los movimientos registrados.
8. El sistema debe actualizar automáticamente los balances cuando se registren nuevos movimientos.

### CA-004-4: Consultar histórico financiero
9. El sistema debe permitir consultar históricos financieros registrados dentro de la plataforma.
10. El sistema debe mostrar la siguiente información de cada movimiento:
    - Fecha del movimiento
    - Categoría
    - Tipo de transacción
    - Responsable del registro
    - Estado del movimiento
    - Valor registrado

### CA-004-5: Filtros de consulta
11. El sistema debe permitir filtrar movimientos financieros según:
    - Fechas
    - Categoría
    - Tipo de movimiento (Ingreso / Egreso)
    - Método de pago
    - Vehículo asociado

### CA-004-6: Trazabilidad
12. El sistema debe generar trazabilidad de todos los registros financieros realizados.

### CA-004-7: Control de acceso
13. El sistema debe restringir el acceso a la información financiera únicamente a usuarios con permisos autorizados.

---

## 4. Categorías de movimientos

### Ingresos

| Categoría | Código | Descripción |
|-----------|--------|-------------|
| Recaudo diario | `RECAUDO` | Dinero recaudado por operación del vehículo en ruta |
| Subsidio / incentivo | `SUBSIDIO` | Pagos de entidades externas (ej. gobierno local) |
| Otro ingreso | `OTRO_INGRESO` | Ingresos extraordinarios no categorizados |

### Egresos

| Categoría | Código | Descripción |
|-----------|--------|-------------|
| Combustible | `COMBUSTIBLE` | Gasto en gasolina o ACPM |
| Mantenimiento preventivo | `MANT_PREVENTIVO` | Revisiones periódicas programadas |
| Mantenimiento correctivo | `MANT_CORRECTIVO` | Reparaciones no programadas |
| Salarios y pagos | `SALARIO` | Pagos a conductores y personal |
| Seguros | `SEGURO` | SOAT, pólizas de responsabilidad civil |
| Documentos y trámites | `TRAMITE` | Renovación de documentación legal |
| Impuestos y tasas | `IMPUESTO` | Rodamiento, impuesto vehicular |
| Otro egreso | `OTRO_EGRESO` | Gastos no categorizados |

---

## 5. Métodos de pago

| Código | Descripción |
|--------|-------------|
| `EFECTIVO` | Pago en efectivo |
| `TRANSFERENCIA` | Transferencia bancaria |

---

## 6. Reglas de negocio

- Los valores deben ser positivos; el tipo (`INGRESO` / `EGRESO`) determina el impacto en el balance.
- No se puede registrar un movimiento con fecha futura.
- El balance de un vehículo se calcula como: `ΣIngresos − ΣEgresos` en el período seleccionado.
- Un egreso de tipo `MANT_PREVENTIVO` o `MANT_CORRECTIVO` puede vincularse automáticamente cuando el vehículo sale del estado `EN_MANTENIMIENTO` (ver RF-002).
- Todo movimiento registrado queda con trazabilidad del usuario que lo creó y la fecha exacta.
- Un movimiento solo puede eliminarse si fue registrado por error el mismo día; pasado ese punto, se registra un movimiento inverso (corrección).

---

## 7. Modelo de datos

```
MovimientoFinanciero
├── id                   Long            PK · autogenerado
├── tipo                 ENUM            NOT NULL  (INGRESO, EGRESO)
├── categoria            ENUM            NOT NULL
│                         (RECAUDO, SUBSIDIO, OTRO_INGRESO,
│                          COMBUSTIBLE, MANT_PREVENTIVO, MANT_CORRECTIVO,
│                          SALARIO, SEGURO, TRAMITE, IMPUESTO, OTRO_EGRESO)
├── metodoPago           ENUM            NOT NULL  (EFECTIVO, TRANSFERENCIA)
├── valor                DECIMAL(15,2)   NOT NULL  CHECK > 0
├── fecha                DATE            NOT NULL
├── descripcion          VARCHAR(300)
├── observaciones        VARCHAR(500)
├── estado               ENUM            NOT NULL  default ACTIVO
│                         (ACTIVO, ANULADO)
├── vehiculo             FK Vehiculo     NOT NULL
├── conductor            FK Conductor    nullable
├── registradoPor        FK Usuario      NOT NULL
├── fechaCreacion        TIMESTAMP       NOT NULL
└── fechaModificacion    TIMESTAMP

Balance (vista calculada / DTO — no tabla persistida)
├── vehiculoId
├── placa
├── periodoDesde
├── periodoHasta
├── totalIngresos
├── totalEgresos
└── utilidadNeta  (totalIngresos − totalEgresos)
```

---

## 8. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/finanzas/movimientos` | Registrar movimiento (ingreso o egreso) | 201 | OWNER, ADMIN |
| `GET` | `/api/finanzas/movimientos` | Listar movimientos con filtros (paginado) | 200 | OWNER, ADMIN |
| `GET` | `/api/finanzas/movimientos/{id}` | Consultar movimiento por ID | 200 | OWNER, ADMIN |
| `PATCH` | `/api/finanzas/movimientos/{id}/anular` | Anular un movimiento | 200 | OWNER |
| `GET` | `/api/finanzas/balance/vehiculo/{vehiculoId}` | Balance de un vehículo por período | 200 | OWNER, ADMIN |
| `GET` | `/api/finanzas/balance/flota` | Balance consolidado de la flota | 200 | OWNER, ADMIN |

**Parámetros de filtro (GET `/api/finanzas/movimientos`):**

| Parámetro | Descripción |
|-----------|-------------|
| `tipo` | INGRESO \| EGRESO |
| `categoria` | Código de categoría (RECAUDO, COMBUSTIBLE, etc.) |
| `metodoPago` | EFECTIVO \| TRANSFERENCIA |
| `vehiculoId` | Filtrar por vehículo |
| `fechaDesde` / `fechaHasta` | Rango de fechas |
| `page` / `size` | Paginación (default: 0 / 20) |

---

## 9. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `RegistrarMovimientoCommand` | Registra un ingreso o egreso |
| `AnularMovimientoCommand` | Marca un movimiento como anulado |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetMovimientoByIdQuery` | Detalle de un movimiento |
| `GetMovimientosByFilterQuery` | Lista paginada con filtros |
| `GetBalanceByVehiculoQuery` | Balance de un vehículo en un período |
| `GetBalanceConsolidadoQuery` | Balance de toda la flota |
| `GetResumenFinancieroQuery` | Indicadores: total ingresos, egresos, utilidad |

---

## 10. Estructura de clases

```
Application/Features/Finanzas/
├── Commands/
│   ├── Registrar/
│   │   ├── RegistrarMovimientoCommand.java
│   │   └── RegistrarMovimientoCommandHandler.java
│   └── Anular/
│       ├── AnularMovimientoCommand.java
│       └── AnularMovimientoCommandHandler.java
└── Queries/
    ├── GetByFilter/
    │   ├── GetMovimientosByFilterQuery.java
    │   └── GetMovimientosByFilterQueryHandler.java
    └── GetBalance/
        ├── GetBalanceByVehiculoQuery.java
        ├── GetBalanceByVehiculoQueryHandler.java
        ├── GetBalanceConsolidadoQuery.java
        └── GetBalanceConsolidadoQueryHandler.java
```
