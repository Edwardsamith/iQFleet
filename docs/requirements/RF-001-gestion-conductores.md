# RF-001 — Gestión de Conductores

| Campo | Detalle |
|-------|---------|
| **ID** | RF-001 |
| **Módulo** | Gestión de Conductores |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-005 (Autenticación y acceso) |
| **Relacionado con** | RF-002 (asignación a vehículo), RF-003 (documentos del conductor), RF-004 (movimientos financieros) |

---

## 1. Descripción

El sistema debe permitir administrar la información de los conductores asociados a los vehículos de la flota. Esto incluye el registro inicial, consulta, actualización de datos, control de estado y monitoreo de vencimiento de licencias. El módulo es prerequisito para la asignación de conductores a vehículos (RF-002) y para el registro de documentos del conductor (RF-003).

---

## 2. Actores

| Actor | Permisos |
|-------|----------|
| Propietario (`ROLE_OWNER`) | Acceso completo: registrar, consultar, actualizar, activar/desactivar |
| Administrador (`ROLE_ADMIN`) | Acceso completo: registrar, consultar, actualizar, activar/desactivar |

---

## 3. Criterios de Aceptación

### CA-001-1: Registrar conductor
1. El sistema debe ofrecer la opción "Registrar conductores" dentro del módulo de gestión de conductores.
2. El sistema debe permitir registrar la siguiente información del conductor:
   - Nombres y apellidos
   - Tipo y número de identificación
   - Número de licencia de conducción
   - Categoría de licencia
   - Fecha de vencimiento de la licencia
   - Número telefónico
   - Correo electrónico
   - Dirección de residencia
   - Estado del conductor
   - Vehículo asignado
   - Fecha de registro
3. El sistema debe validar que no existan conductores registrados con el mismo número de identificación o número de licencia.

### CA-001-2: Consultar conductores
4. El sistema debe ofrecer la opción "Consultar conductores" dentro del módulo de gestión de conductores.
5. El sistema debe permitir visualizar la información registrada de los conductores de la flota.
6. El sistema debe permitir consultar información relacionada con:
   - Estado del conductor
   - Licencia de conducción
   - Fecha de vencimiento de documentos
   - Vehículo asignado
   - Fecha de registro

### CA-001-3: Actualizar conductor
7. El sistema debe ofrecer la opción "Actualizar registro de conductores" dentro del módulo de gestión de conductores.
8. El sistema debe permitir modificar la información previamente registrada de un conductor.

### CA-001-4: Activar / Desactivar conductor
9. El sistema debe ofrecer la opción "Activar/Desactivar conductores" dentro del módulo de gestión de conductores.
10. El sistema debe permitir cambiar el estado del conductor entre: **Activo** e **Inactivo**.
11. El sistema debe reflejar el estado actual del conductor dentro de las consultas realizadas.

### CA-001-5: Alertas de vencimiento
12. El sistema debe generar alertas cuando la licencia de conducción o documentos asociados se encuentren próximos a vencer.

### CA-001-6: Filtros de consulta
13. El sistema debe permitir filtrar conductores según:
    - Estado
    - Vehículo asignado
    - Categoría de licencia
    - Fecha de vencimiento
    - Fecha de registro

### CA-001-7: Control de acceso
14. El sistema debe restringir el acceso a las funcionalidades de gestión de conductores únicamente a usuarios con permisos autorizados.

---

## 4. Reglas de negocio

- Un conductor con estado `INACTIVO` no puede ser asignado a ningún vehículo.
- Un conductor con licencia vencida se marca con alerta `LICENCIA_VENCIDA`; el sistema no bloquea su consulta pero impide su asignación.
- No se puede eliminar un conductor que tenga documentos registrados o movimientos financieros vinculados; solo puede desactivarse.
- La cédula y el número de licencia son únicos a nivel del sistema.
- Al desactivar un conductor con vehículo asignado, el sistema debe desasignarlo previamente.

---

## 5. Validaciones de campos

| Campo | Obligatorio | Restricciones |
|-------|-------------|---------------|
| Nombres y apellidos | Sí | 2–100 caracteres, solo letras y espacios |
| Tipo de identificación | Sí | CC, CE, PA |
| Número de identificación | Sí | 6–12 dígitos. Único en el sistema |
| Número de licencia | Sí | 5–20 caracteres alfanuméricos. Único |
| Categoría de licencia | Sí | A1, A2, B1, B2, B3, C1, C2, C3 |
| Fecha de vencimiento licencia | Sí | Fecha válida (puede estar vencida al registrar) |
| Número telefónico | No | 7–15 dígitos |
| Correo electrónico | No | Formato de email válido. Único si se ingresa |
| Dirección de residencia | No | Máximo 200 caracteres |
| Estado | Sí | ACTIVO \| INACTIVO (default: ACTIVO) |
| Fecha de registro | No | No puede ser futura |

---

## 6. Modelo de datos

```
Conductor
├── id                   Long            PK · autogenerado
├── tipoIdentificacion   VARCHAR(3)      NOT NULL  (CC, CE, PA)
├── numeroIdentificacion VARCHAR(12)     UNIQUE · NOT NULL
├── nombres              VARCHAR(100)    NOT NULL
├── apellidos            VARCHAR(100)    NOT NULL
├── numeroLicencia       VARCHAR(20)     UNIQUE · NOT NULL
├── categoriaLicencia    VARCHAR(3)      NOT NULL  (A1, A2, B1, B2, B3, C1, C2, C3)
├── vencimientoLicencia  DATE            NOT NULL
├── telefono             VARCHAR(15)
├── email                VARCHAR(150)    UNIQUE
├── direccion            VARCHAR(200)
├── estado               ENUM            NOT NULL  default ACTIVO
├── vehiculoAsignado     FK Vehiculo     nullable
├── fechaRegistro        DATE
├── fechaBaja            DATE
├── motivoBaja           VARCHAR(300)
├── fechaCreacion        TIMESTAMP       NOT NULL
└── fechaModificacion    TIMESTAMP

Relaciones:
  Conductor 1──N Documento              (documentos del conductor)
  Conductor 0──1 Vehiculo               (vehículo actualmente asignado)
  Conductor 0──N MovimientoFinanciero   (pagos vinculados)
```

---

## 7. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/conductores` | Registrar conductor | 201 | OWNER, ADMIN |
| `GET` | `/api/conductores` | Listar conductores con filtros (paginado) | 200 | OWNER, ADMIN |
| `GET` | `/api/conductores/{id}` | Consultar conductor por ID | 200 | OWNER, ADMIN |
| `PUT` | `/api/conductores/{id}` | Actualizar datos del conductor | 200 | OWNER, ADMIN |
| `PATCH` | `/api/conductores/{id}/estado` | Activar o desactivar conductor | 200 | OWNER, ADMIN |
| `DELETE` | `/api/conductores/{id}` | Eliminar (solo sin historial) | 204 | OWNER |

**Parámetros de filtro (GET `/api/conductores`):**

| Parámetro | Descripción |
|-----------|-------------|
| `estado` | ACTIVO \| INACTIVO |
| `vehiculoId` | Filtrar por vehículo asignado |
| `categoriaLicencia` | A1, A2, B1, B2, B3, C1, C2, C3 |
| `venceEn` | Días restantes para vencer (ej. 30) |
| `fechaDesde` / `fechaHasta` | Rango de fecha de registro |
| `page` / `size` | Paginación (default: 0 / 20) |

---

## 8. Comandos y Queries (CQRS)

### Commands
| Clase | Descripción |
|-------|-------------|
| `CreateConductorCommand` | Registra un nuevo conductor |
| `UpdateConductorCommand` | Actualiza campos del conductor |
| `CambiarEstadoConductorCommand` | Cambia estado ACTIVO ↔ INACTIVO |
| `AsignarVehiculoCommand` | Vincula el conductor a un vehículo |

### Queries
| Clase | Descripción |
|-------|-------------|
| `GetConductorByIdQuery` | Detalle completo del conductor |
| `GetConductoresByFilterQuery` | Lista paginada con filtros |
| `GetConductoresActivosQuery` | Solo conductores en estado ACTIVO |
| `GetConductoresConLicenciaProximaAVencerQuery` | Conductores cuya licencia vence en N días |

---

## 9. Estructura de clases

```
Application/Features/Conductores/
├── Commands/
│   ├── Create/
│   │   ├── CreateConductorCommand.java
│   │   └── CreateConductorCommandHandler.java
│   ├── Update/
│   │   ├── UpdateConductorCommand.java
│   │   └── UpdateConductorCommandHandler.java
│   └── CambiarEstado/
│       ├── CambiarEstadoConductorCommand.java
│       └── CambiarEstadoConductorCommandHandler.java
└── Queries/
    ├── GetById/
    │   ├── GetConductorByIdQuery.java
    │   └── GetConductorByIdQueryHandler.java
    └── GetByFilter/
        ├── GetConductoresByFilterQuery.java
        └── GetConductoresByFilterQueryHandler.java
```
