# RF-006 — Solicitud y Registro de Usuarios

| Campo | Detalle |
|-------|---------|
| **ID** | RF-006 |
| **Módulo** | Registro de Usuarios |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-005 (autenticación — necesario para la aprobación por el administrador) |
| **Relacionado con** | RF-007 (recuperación de contraseña), RF-008 (gestión de usuarios) |

---

## 1. Descripción

El sistema debe permitir el registro de nuevos usuarios mediante un formulario de solicitud de acceso, gestionando la validación, almacenamiento y aprobación de la información suministrada. Las solicitudes quedan en estado pendiente hasta que un administrador las apruebe o rechace.

---

## 2. Actores

| Actor | Rol |
|-------|-----|
| Usuario solicitante | Persona que solicita acceso a la plataforma; no está autenticada |
| Administrador / Propietario (`ROLE_OWNER`, `ROLE_ADMIN`) | Revisa, aprueba o rechaza las solicitudes |
| Sistema (iQFleet) | Valida datos, registra la solicitud y gestiona los cambios de estado |

---

## 3. Criterios de Aceptación

### CA-006-1: Formulario de registro
1. El sistema debe ofrecer un formulario de registro para nuevos usuarios.
2. El formulario de registro debe permitir diligenciar la siguiente información:
   - Nombres y apellidos
   - Tipo y número de identificación
   - Correo electrónico
   - Número telefónico
   - Nombre de usuario
   - Contraseña
   - Rol solicitado

### CA-006-2: Validación de campos
3. El sistema debe validar que los campos obligatorios se encuentren diligenciados correctamente.

### CA-006-3: Unicidad
4. El sistema debe validar que no existan usuarios registrados con el mismo:
   - Correo electrónico
   - Número de identificación
   - Nombre de usuario

### CA-006-4: Consulta de solicitudes
5. El sistema debe permitir al administrador consultar las solicitudes de acceso registradas.

### CA-006-5: Aprobación o rechazo
6. El sistema debe permitir aprobar o rechazar las solicitudes de acceso realizadas por los usuarios.

### CA-006-6: Actualización de estado
7. El sistema debe actualizar el estado de la solicitud según la decisión tomada:
   - **Aprobada**
   - **Rechazada**

### CA-006-7: Acceso aprobado
8. Si la solicitud es aprobada, el sistema debe habilitar el acceso del usuario a la plataforma.

### CA-006-8: Acceso rechazado
9. Si la solicitud es rechazada, el sistema debe impedir el acceso del usuario y mantener el registro de la solicitud.

---

## 4. Flujo del proceso de registro

```
[Usuario] Completa formulario de solicitud
           │
           ▼
[Sistema]  Valida campos obligatorios y unicidad
           │ error → retorna mensajes de validación
           │ ok
           ▼
[Sistema]  Guarda solicitud con estado PENDIENTE
           │
           ▼
[Admin]    Consulta listado de solicitudes pendientes
           │
           ├── Aprueba solicitud
           │       │
           │       ▼
           │   [Sistema] Cambia estado → APROBADA
           │             Activa cuenta del usuario (estado ACTIVO)
           │             El usuario puede iniciar sesión
           │
           └── Rechaza solicitud
                   │
                   ▼
               [Sistema] Cambia estado → RECHAZADA
                         Usuario no puede iniciar sesión
                         Registro se conserva
```

---

## 5. Reglas de negocio

- Un usuario con solicitud en estado `PENDIENTE` o `RECHAZADA` no puede iniciar sesión.
- El rol solicitado puede ser modificado por el administrador al momento de aprobar.
- Las contraseñas se almacenan como hash BCrypt desde el momento del registro, nunca en texto plano.
- El nombre de usuario no puede contener espacios ni caracteres especiales distintos de `_` y `.`.
- El correo electrónico es el identificador principal para el inicio de sesión (RF-005).

---

## 6. Validaciones de campos

| Campo | Obligatorio | Restricciones |
|-------|-------------|---------------|
| Nombres y apellidos | Sí | 2–100 caracteres, solo letras y espacios |
| Tipo de identificación | Sí | CC, CE, PA |
| Número de identificación | Sí | 6–12 dígitos. Único |
| Correo electrónico | Sí | Formato válido. Único |
| Número telefónico | No | 7–15 dígitos |
| Nombre de usuario | Sí | 4–30 caracteres. Letras, números, `_`, `.`. Único |
| Contraseña | Sí | Mínimo 8 caracteres, al menos 1 mayúscula y 1 número |
| Rol | Sí | ROLE_OWNER, ROLE_ADMIN |

---

## 7. Modelo de datos

El modelo de `Usuario` se define en RF-005. El estado del proceso de registro se maneja con el campo `estado` del usuario:

| Estado | Descripción |
|--------|-------------|
| `PENDIENTE` | Solicitud registrada, pendiente de revisión |
| `ACTIVO` | Solicitud aprobada; usuario puede acceder |
| `RECHAZADO` | Solicitud rechazada; sin acceso |
| `INACTIVO` | Usuario desactivado por el administrador |
| `BLOQUEADO` | Bloqueado por intentos fallidos (ver RF-005) |

```
SolicitudRegistro  (registro de auditoría de la decisión)
├── id                   Long
├── usuario              FK Usuario
├── estadoDecision       ENUM  (APROBADA, RECHAZADA)
├── motivoRechazo        VARCHAR(300)  nullable
├── revisadoPor          FK Usuario    (admin que tomó la decisión)
└── fechaDecision        TIMESTAMP
```

---

## 8. Endpoints API REST

| Método | Ruta | Descripción | Código | Autenticación |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/auth/registro` | Enviar solicitud de registro | 201 | No requerida |
| `GET` | `/api/usuarios/solicitudes` | Listar solicitudes pendientes | 200 | OWNER, ADMIN |
| `PATCH` | `/api/usuarios/solicitudes/{id}/aprobar` | Aprobar solicitud | 200 | OWNER, ADMIN |
| `PATCH` | `/api/usuarios/solicitudes/{id}/rechazar` | Rechazar solicitud | 200 | OWNER, ADMIN |

---

## 9. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `SolicitarRegistroCommand` | Registra la solicitud de acceso del usuario |
| `AprobarSolicitudCommand` | Aprueba y activa la cuenta del usuario |
| `RechazarSolicitudCommand` | Rechaza la solicitud y bloquea el acceso |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetSolicitudesPendientesQuery` | Lista solicitudes en estado PENDIENTE |
| `GetSolicitudByIdQuery` | Detalle de una solicitud específica |
