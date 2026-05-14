# RF-008 — Gestión de Usuarios

| Campo | Detalle |
|-------|---------|
| **ID** | RF-008 |
| **Módulo** | Gestión de Usuarios |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-005 (autenticación), RF-006 (registro) |
| **Relacionado con** | RF-009 (panel administrativo) |

---

## 1. Descripción

El sistema debe permitir administrar la información, estado y permisos de los usuarios registrados dentro de la plataforma. Este módulo está disponible exclusivamente para usuarios con permisos administrativos (`ROLE_OWNER`, `ROLE_ADMIN`), y permite crear, consultar, actualizar y activar/desactivar cuentas de usuario.

---

## 2. Actores

| Actor | Permisos |
|-------|----------|
| Propietario (`ROLE_OWNER`) | Acceso completo: crear, consultar, actualizar, activar/desactivar |
| Administrador (`ROLE_ADMIN`) | Consultar usuarios y cambiar estado; no puede crear nuevos usuarios con rol OWNER |

---

## 3. Criterios de Aceptación

### CA-008-1: Acceso al módulo
1. El sistema debe ofrecer un módulo de gestión de usuarios.

### CA-008-2: Crear usuario
2. El sistema debe permitir crear nuevos usuarios mediante un formulario de registro.
3. El formulario de registro debe permitir ingresar:
   - Nombres y apellidos
   - Tipo y número de identificación
   - Correo electrónico
   - Número telefónico
   - Nombre de usuario
   - Contraseña
   - Estado del usuario
   - Rol o permisos asignados

### CA-008-3: Unicidad
4. El sistema debe validar que no existan usuarios duplicados con el mismo:
   - Correo electrónico
   - Número de identificación
   - Nombre de usuario

### CA-008-4: Consultar usuarios
5. El sistema debe almacenar y permitir consultar la información registrada de los usuarios.
6. El sistema debe permitir ver la siguiente información por usuario:
   - Datos personales
   - Estado de la cuenta
   - Fecha de registro
   - Último acceso
   - Rol

### CA-008-5: Actualizar usuario
7. El sistema debe permitir actualizar la información previamente registrada de los usuarios.
8. El sistema debe permitir modificar:
   - Información personal
   - Estado del usuario

### CA-008-6: Activar / Desactivar usuario
9. El sistema debe permitir activar o desactivar usuarios.
10. El sistema debe reflejar el estado actual del usuario.

### CA-008-7: Control de acceso
11. El sistema debe restringir el acceso a este módulo únicamente a usuarios con permisos administrativos.

---

## 4. Reglas de negocio

- Un usuario no puede desactivar su propia cuenta.
- Solo `ROLE_OWNER` puede crear usuarios con rol `ROLE_OWNER`.
- Un usuario desactivado (`INACTIVO`) no puede iniciar sesión. Sus registros históricos se conservan.
- El correo electrónico y el nombre de usuario no pueden ser modificados una vez aprobado el registro (son identificadores del sistema). Si se requiere cambio, debe gestionarse con soporte.
- Al desactivar un usuario, sus tokens activos deben invalidarse inmediatamente.
- La contraseña solo puede ser cambiada por el propio usuario (via RF-007) o por un OWNER como reset forzado.

---

## 5. Validaciones de campos

| Campo | Obligatorio | Restricciones |
|-------|-------------|---------------|
| Nombres y apellidos | Sí | 2–100 caracteres |
| Tipo de identificación | Sí | CC, CE, PA |
| Número de identificación | Sí | 6–12 dígitos. Único |
| Correo electrónico | Sí | Formato válido. Único |
| Número telefónico | No | 7–15 dígitos |
| Nombre de usuario | Sí | 4–30 caracteres. Sin espacios. Único |
| Contraseña | Sí | Mínimo 8 caracteres, 1 mayúscula, 1 número |
| Estado | Sí | ACTIVO, INACTIVO |
| Rol | Sí | ROLE_OWNER, ROLE_ADMIN |

---

## 6. Modelo de datos

Ver modelo de `Usuario` en [RF-005](RF-005-autenticacion-usuarios.md).

---

## 7. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/usuarios` | Crear usuario | 201 | OWNER, ADMIN |
| `GET` | `/api/usuarios` | Listar usuarios (paginado, con filtros) | 200 | OWNER, ADMIN |
| `GET` | `/api/usuarios/{id}` | Consultar usuario por ID | 200 | OWNER, ADMIN |
| `PUT` | `/api/usuarios/{id}` | Actualizar datos del usuario | 200 | OWNER, ADMIN |
| `PATCH` | `/api/usuarios/{id}/estado` | Activar o desactivar usuario | 200 | OWNER, ADMIN |
| `PATCH` | `/api/usuarios/{id}/reset-password` | Reset forzado de contraseña | 200 | OWNER |

**Parámetros de filtro (GET `/api/usuarios`):**

| Parámetro | Descripción |
|-----------|-------------|
| `estado` | ACTIVO \| INACTIVO \| BLOQUEADO |
| `rol` | ROLE_OWNER \| ROLE_ADMIN |
| `page` / `size` | Paginación (default: 0 / 20) |

---

## 8. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `CreateUsuarioCommand` | Crea un usuario directamente (sin flujo de solicitud) |
| `UpdateUsuarioCommand` | Actualiza datos personales del usuario |
| `CambiarEstadoUsuarioCommand` | Activa o desactiva la cuenta |
| `ResetPasswordCommand` | Fuerza el restablecimiento de contraseña |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetUsuarioByIdQuery` | Detalle completo del usuario |
| `GetUsuariosByFilterQuery` | Lista paginada con filtros |
| `GetUsuariosActivosQuery` | Solo usuarios en estado ACTIVO |
