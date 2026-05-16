# RF-003 — Gestión Documental

| Campo | Detalle |
|-------|---------|
| **ID** | RF-003 |
| **Módulo** | Gestión Documental |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-001 (conductores), RF-002 (vehículos), RF-005 (autenticación) |
| **Relacionado con** | RF-009 (vista de métricas — alertas documentales), RF-010 (reportes) |

---

## 1. Descripción

El sistema debe permitir administrar la documentación asociada a los vehículos, conductores y procesos administrativos. El control de fechas de vencimiento y la generación de alertas son las funcionalidades más críticas de este módulo: un vencimiento no detectado a tiempo puede generar multas, inmovilización de vehículos y sanciones legales.

---

## 2. Actores

| Actor | Permisos |
|-------|----------|
| Propietario (`ROLE_OWNER`) | Acceso completo |
| Administrador (`ROLE_ADMIN`) | Cargar, consultar, actualizar documentos; ver alertas |
| Sistema (iQFleet) | Calcula estado de vigencia y genera alertas automáticas |

---

## 3. Criterios de Aceptación

### CA-003-1: Acceso al módulo
1. El sistema debe contar con un módulo de gestión documental para usuarios autorizados.

### CA-003-2: Cargar documento
2. El sistema debe permitir cargar documentos digitales relacionados con:
   - Vehículos
   - Conductores
   - Procesos administrativos
   - Registros operativos
3. El sistema debe permitir registrar la siguiente información asociada al documento:
   - Nombre del documento
   - Tipo de documento
   - Fecha de emisión
   - Fecha de vencimiento
   - Responsable asociado (conductor o vehículo)
   - Estado del documento
4. El sistema debe permitir almacenar documentos en formato digital.
5. El sistema debe validar el formato del archivo cargado.

### CA-003-3: Consultar documentos
6. El sistema debe permitir consultar los documentos almacenados.
7. El sistema debe permitir visualizar la siguiente información de cada documento:
   - Tipo de documento
   - Estado de vigencia
   - Fecha de vencimiento
   - Vehículo o conductor asociado
   - Fecha de carga

### CA-003-4: Actualizar / renovar documento
8. El sistema debe permitir actualizar, reemplazar o cargar nuevas versiones de los documentos previamente registrados.

### CA-003-5: Control de vencimientos
9. El sistema debe registrar y controlar las fechas de vencimiento de los documentos asociados.
10. El sistema debe generar recordatorios automáticos cuando un documento se encuentre próximo a vencer.
11. El sistema debe permitir configurar alertas de vencimiento según periodos definidos en la plataforma.

### CA-003-6: Estados del documento
12. El sistema debe mostrar el estado actual del documento como:
    - **Próximo a vencer** (dentro del periodo de alerta configurado)
    - **Vencido** (fecha de vencimiento superada)
    - **Inactivo** (dado de baja manualmente)
    - **Vigente** (dentro del periodo de validez)

### CA-003-7: Filtros de consulta
13. El sistema debe permitir filtrar documentos según:
    - Tipo de documento
    - Estado
    - Fecha de vencimiento
    - Vehículo o conductor asociado

### CA-003-8: Control de acceso
14. El sistema debe restringir el acceso a la gestión documental únicamente a usuarios con permisos autorizados.

---

## 4. Tipos de documentos gestionados

### Documentos de vehículo

| Tipo | Descripción | Periodicidad | Obligatorio |
|------|-------------|-------------|-------------|
| `SOAT` | Seguro Obligatorio de Accidentes de Tránsito | Anual | Sí |
| `RTM` | Revisión Técnico-Mecánica y de Gases | Anual / bianual | Sí |
| `TARJETA_OPERACION` | Habilitación para operar en ruta | Periódico | Sí |
| `TARJETA_PROPIEDAD` | Certificado de propiedad del vehículo | Sin vencimiento | Sí |
| `POLIZA_RC` | Póliza de Responsabilidad Civil Extracontractual | Anual | Sí |
| `OTRO_VEHICULO` | Cualquier otro documento del vehículo | Variable | No |

### Documentos de conductor

| Tipo | Descripción | Periodicidad | Obligatorio |
|------|-------------|-------------|-------------|
| `LICENCIA_CONDUCCION` | Licencia de conducción habilitante | Variable | Sí |
| `CERTIFICADO_MEDICO` | Certificado de aptitud psicofísica | Periódico | Sí |
| `CONTRATO_VINCULACION` | Contrato laboral o de vinculación | Variable | Recomendado |
| `PAZ_SALVO_TRANSITO` | Sin comparendos pendientes | Variable | No |
| `OTRO_CONDUCTOR` | Cualquier otro documento del conductor | Variable | No |

### Documentos administrativos

| Tipo | Descripción |
|------|-------------|
| `HABILITACION_EMPRESA` | Habilitación de la empresa de transporte |
| `CONTRATO_RUTA` | Contrato de operación de ruta |
| `OTRO_ADMIN` | Documentos administrativos varios |

---

## 5. Lógica de cálculo de estados

El estado de un documento se calcula automáticamente en cada consulta:

```
si fechaVencimiento es null:
    estado = SIN_VENCIMIENTO

si fechaVencimiento < hoy:
    estado = VENCIDO

si fechaVencimiento <= hoy + diasAlerta (default 30):
    estado = PROXIMO_A_VENCER

si fechaVencimiento > hoy + diasAlerta:
    estado = VIGENTE
```

El período de alerta es configurable por instalación (default: 30 días).

---

## 6. Reglas de negocio

- Un documento de tipo `SOAT` o `RTM` vencido genera una alerta crítica sobre el vehículo asociado.
- Un vehículo sin `SOAT` o `RTM` registrado también aparece en el panel de alertas.
- Un conductor sin `LICENCIA_CONDUCCION` registrada no puede ser asignado a ningún vehículo.
- Conductor y vehículo son mutuamente excluyentes como entidad propietaria del documento. Un documento pertenece a uno o al otro, nunca a ambos.
- La carga de un nuevo archivo para un documento existente crea una nueva versión; la versión anterior se conserva en el historial.

---

## 7. Modelo de datos

```
Documento
├── id                   Long            PK · autogenerado
├── nombre               VARCHAR(150)    NOT NULL
├── tipo                 ENUM            NOT NULL
│                         (SOAT, RTM, TARJETA_OPERACION, TARJETA_PROPIEDAD,
│                          POLIZA_RC, LICENCIA_CONDUCCION, CERTIFICADO_MEDICO,
│                          CONTRATO_VINCULACION, PAZ_SALVO_TRANSITO, ...)
├── numeroReferencia     VARCHAR(50)     (código o número del documento)
├── entidadEmisora       VARCHAR(100)
├── fechaEmision         DATE
├── fechaVencimiento     DATE            (nullable para SIN_VENCIMIENTO)
├── estado               ENUM calculado  (VIGENTE, PROXIMO_A_VENCER, VENCIDO, INACTIVO)
├── archivoUrl           VARCHAR(500)    (ruta del archivo digital almacenado)
├── archivoFormato       VARCHAR(10)     (PDF, JPG, PNG)
├── observaciones        VARCHAR(500)
├── conductor            FK Conductor    nullable
├── vehiculo             FK Vehiculo     nullable
├── cargadoPor           FK Usuario      NOT NULL
├── fechaCarga           TIMESTAMP       NOT NULL
└── fechaModificacion    TIMESTAMP

VersionDocumento  (historial de versiones)
├── id                   Long
├── documento            FK Documento
├── archivoUrl           VARCHAR(500)
├── fechaVencimientoAnterior  DATE
├── reemplazadoPor       FK Usuario
└── fechaReemplazo       TIMESTAMP
```

---

## 8. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/documentos` | Registrar / cargar documento | 201 | OWNER, ADMIN |
| `GET` | `/api/documentos` | Listar documentos con filtros (paginado) | 200 | OWNER, ADMIN |
| `GET` | `/api/documentos/{id}` | Consultar documento por ID | 200 | OWNER, ADMIN |
| `GET` | `/api/documentos/conductor/{conductorId}` | Documentos de un conductor | 200 | OWNER, ADMIN |
| `GET` | `/api/documentos/vehiculo/{vehiculoId}` | Documentos de un vehículo | 200 | OWNER, ADMIN |
| `GET` | `/api/documentos/alertas` | Documentos próximos a vencer o vencidos | 200 | OWNER, ADMIN |
| `PUT` | `/api/documentos/{id}` | Renovar / actualizar documento | 200 | OWNER, ADMIN |
| `PATCH` | `/api/documentos/{id}/estado` | Marcar como inactivo | 200 | OWNER, ADMIN |
| `DELETE` | `/api/documentos/{id}` | Eliminar documento | 204 | OWNER |

**Parámetros de filtro (GET `/api/documentos`):**

| Parámetro | Descripción |
|-----------|-------------|
| `tipo` | Tipo de documento (SOAT, RTM, etc.) |
| `estado` | VIGENTE, PROXIMO_A_VENCER, VENCIDO, INACTIVO |
| `vehiculoId` | Filtrar por vehículo |
| `conductorId` | Filtrar por conductor |
| `venceAntesDe` | Documentos que vencen antes de esta fecha |
| `page` / `size` | Paginación (default: 0 / 20) |

---

## 9. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `CreateDocumentoCommand` | Registra un nuevo documento |
| `RenovarDocumentoCommand` | Crea nueva versión del documento |
| `CambiarEstadoDocumentoCommand` | Marca documento como inactivo |
| `DeleteDocumentoCommand` | Elimina documento sin historial |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetDocumentoByIdQuery` | Detalle completo |
| `GetDocumentosByVehiculoQuery` | Documentos de un vehículo |
| `GetDocumentosByConductorQuery` | Documentos de un conductor |
| `GetDocumentosAlertasQuery` | Vencidos y próximos a vencer |
| `GetDocumentosByFilterQuery` | Lista paginada con filtros |
