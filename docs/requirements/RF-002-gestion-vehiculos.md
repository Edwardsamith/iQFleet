# RF-002 — Gestión de Vehículos

| Campo | Detalle |
|-------|---------|
| **ID** | RF-002 |
| **Módulo** | Gestión de Vehículos |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-001 (conductores), RF-005 (autenticación) |
| **Relacionado con** | RF-003 (documentos del vehículo), RF-004 (movimientos financieros) |

---

## 1. Descripción

El sistema debe permitir administrar toda la información de los vehículos pertenecientes a la flota. Esto incluye el registro con datos técnicos, consulta con filtros, actualización, habilitación/deshabilitación y control del estado operativo de cada unidad.

---

## 2. Actores

| Actor | Permisos |
|-------|----------|
| Propietario (`ROLE_OWNER`) | Acceso completo |
| Administrador (`ROLE_ADMIN`) | Acceso completo: registrar, consultar, actualizar, habilitar/deshabilitar |

---

## 3. Criterios de Aceptación

### CA-002-1: Acceso al módulo
1. El sistema debe ofrecer un módulo de gestión de vehículos para usuarios autorizados.

### CA-002-2: Registrar vehículo
2. El sistema debe ofrecer la opción "Registrar vehículos" dentro del módulo de gestión de vehículos.
3. El sistema debe permitir registrar la siguiente información del vehículo:
   - Placa
   - Marca
   - Modelo
   - Tipo de vehículo
   - Año
   - Estado del vehículo
   - Propietario o responsable asignado
   - Fecha de registro
4. El sistema debe validar que no existan vehículos registrados con la misma placa.
5. El sistema debe almacenar la información registrada de los vehículos.

### CA-002-3: Consultar vehículos
6. El sistema debe ofrecer la opción "Consultar vehículos" dentro del módulo de gestión de vehículos.
7. El sistema debe permitir visualizar la información registrada de todos los vehículos de la flota.
8. El sistema debe permitir consultar información relacionada con:
   - Estado del vehículo
   - Documentación asociada
   - Fecha de registro
   - Responsable asignado

### CA-002-4: Actualizar vehículo
9. El sistema debe ofrecer la opción "Actualizar registro de vehículos" dentro del módulo de gestión de vehículos.
10. El sistema debe permitir modificar toda la información de los vehículos previamente registrados.

### CA-002-5: Habilitar / Deshabilitar vehículo
11. El sistema debe ofrecer la opción "Habilitar/Deshabilitar vehículos" dentro del módulo de gestión de vehículos.
12. El sistema debe permitir cambiar el estado de un vehículo entre:
    - **Activo**
    - **Inactivo**
    - **En mantenimiento**
13. El sistema debe reflejar el estado actual del vehículo dentro de las consultas.

### CA-002-6: Control de acceso
14. El sistema debe restringir el acceso de funcionalidades de gestión de vehículos únicamente a usuarios autorizados.

---

## 4. Reglas de negocio

- La placa es el identificador único del vehículo en el sistema. No puede modificarse una vez registrada.
- Un vehículo con estado `EN_MANTENIMIENTO` o `INACTIVO` no puede tener conductor asignado activo.
- Al cambiar el estado a `EN_MANTENIMIENTO`, el sistema desasigna automáticamente al conductor activo y registra la fecha de fin de la asignación.
- Al salir de mantenimiento, el costo registrado genera automáticamente un egreso en el módulo financiero (RF-004).
- Un vehículo no puede eliminarse si tiene documentos registrados o movimientos financieros históricos; solo puede deshabilitarse.
- El sistema debe alertar cuando el SOAT o la Revisión Técnico-Mecánica estén próximos a vencer (ver RF-003).

---

## 5. Validaciones de campos

| Campo | Obligatorio | Restricciones |
|-------|-------------|---------------|
| Placa | Sí | Formato colombiano: ABC-123 o ABC-12D. Único |
| Marca | Sí | 2–50 caracteres |
| Modelo | Sí | 2–80 caracteres |
| Tipo de vehículo | Sí | BUS, BUSETA, MICROBUS, VAN |
| Año | No | Entre 1990 y año actual + 1 |
| Estado | Sí | ACTIVO, EN_MANTENIMIENTO, INACTIVO (default: ACTIVO) |
| Responsable asignado | No | FK a Conductor o nombre del propietario |
| Fecha de registro | No | No puede ser futura |

---

## 6. Modelo de datos

```
Vehiculo
├── id                   Long            PK · autogenerado
├── placa                VARCHAR(10)     UNIQUE · NOT NULL
├── marca                VARCHAR(50)     NOT NULL
├── modelo               VARCHAR(80)     NOT NULL
├── tipoVehiculo         ENUM            NOT NULL  (BUS, BUSETA, MICROBUS, VAN)
├── anio                 INTEGER
├── estado               ENUM            NOT NULL  default ACTIVO
│                         (ACTIVO | EN_MANTENIMIENTO | INACTIVO)
├── conductorAsignado    FK Conductor    nullable
├── responsable          VARCHAR(100)    (nombre del propietario/responsable)
├── fechaRegistro        DATE
├── observaciones        VARCHAR(500)
├── fechaCreacion        TIMESTAMP       NOT NULL
└── fechaModificacion    TIMESTAMP

Relaciones:
  Vehiculo 1──N Documento              (documentos del vehículo)
  Vehiculo 1──N MovimientoFinanciero   (ingresos y egresos)
  Vehiculo 0──1 Conductor              (conductor actualmente asignado)

HistorialAsignacion
├── id                   Long
├── vehiculo             FK Vehiculo
├── conductor            FK Conductor
├── fechaInicio          DATE
└── fechaFin             DATE (null si es la asignación activa)
```

---

## 7. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/vehiculos` | Registrar vehículo | 201 | OWNER, ADMIN |
| `GET` | `/api/vehiculos` | Listar vehículos con filtros (paginado) | 200 | OWNER, ADMIN |
| `GET` | `/api/vehiculos/{id}` | Consultar vehículo por ID | 200 | OWNER, ADMIN |
| `PUT` | `/api/vehiculos/{id}` | Actualizar datos del vehículo | 200 | OWNER, ADMIN |
| `PATCH` | `/api/vehiculos/{id}/estado` | Cambiar estado operativo | 200 | OWNER, ADMIN |
| `PATCH` | `/api/vehiculos/{id}/conductor` | Asignar/desasignar conductor | 200 | OWNER, ADMIN |
| `GET` | `/api/vehiculos/{id}/historial` | Historial de asignaciones | 200 | OWNER, ADMIN |
| `DELETE` | `/api/vehiculos/{id}` | Eliminar (solo sin historial) | 204 | OWNER |

**Parámetros de filtro (GET `/api/vehiculos`):**

| Parámetro | Descripción |
|-----------|-------------|
| `placa` | Búsqueda parcial o exacta por placa |
| `marca` | Filtro por marca |
| `estado` | ACTIVO \| EN_MANTENIMIENTO \| INACTIVO |
| `sinConductor` | true = solo vehículos sin conductor asignado |
| `page` / `size` | Paginación (default: 0 / 20) |

---

## 8. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `CreateVehiculoCommand` | Registra un nuevo vehículo |
| `UpdateVehiculoCommand` | Actualiza los campos del vehículo |
| `CambiarEstadoVehiculoCommand` | Gestiona transiciones de estado |
| `AsignarConductorCommand` | Vincula un conductor al vehículo |
| `DesasignarConductorCommand` | Libera al conductor del vehículo |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetVehiculoByIdQuery` | Detalle completo del vehículo |
| `GetVehiculosByFilterQuery` | Lista paginada con filtros |
| `GetVehiculosActivosQuery` | Solo vehículos en estado ACTIVO |
| `GetHistorialAsignacionesQuery` | Historial de conductores de un vehículo |

---

## 9. Estructura de clases

```
Application/Features/Vehiculos/
├── Commands/
│   ├── Create/
│   │   ├── CreateVehiculoCommand.java
│   │   └── CreateVehiculoCommandHandler.java
│   ├── Update/
│   │   ├── UpdateVehiculoCommand.java
│   │   └── UpdateVehiculoCommandHandler.java
│   ├── CambiarEstado/
│   │   ├── CambiarEstadoVehiculoCommand.java
│   │   └── CambiarEstadoVehiculoCommandHandler.java
│   └── AsignarConductor/
│       ├── AsignarConductorCommand.java
│       └── AsignarConductorCommandHandler.java
└── Queries/
    ├── GetById/
    │   ├── GetVehiculoByIdQuery.java
    │   └── GetVehiculoByIdQueryHandler.java
    └── GetByFilter/
        ├── GetVehiculosByFilterQuery.java
        └── GetVehiculosByFilterQueryHandler.java
```
