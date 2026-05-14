# RF-005 — Gestión de Autenticación y Acceso

| Campo | Detalle |
|-------|---------|
| **ID** | RF-005 |
| **Módulo** | Autenticación y Acceso |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | — (módulo base, prerequisito de todos los demás) |
| **Relacionado con** | RF-006 (recuperación de contraseña), RF-008 (gestión de usuarios) |

---

## 1. Descripción

El sistema debe permitir a los usuarios autenticarse de manera segura mediante credenciales de acceso. La autenticación controla el ingreso a la plataforma y la sesión gestiona el tiempo de acceso activo. El acceso a módulos, funcionalidades y opciones del sistema se restringe según el rol asignado al usuario.

El proyecto incluye `spring-boot-starter-security` como dependencia base para la implementación de este módulo mediante JWT (JSON Web Token).

---

## 2. Actores

| Actor | Descripción |
|-------|-------------|
| Propietario (`ROLE_OWNER`) | Acceso total al sistema |
| Administrador (`ROLE_ADMIN`) | Acceso operativo; sin gestión de usuarios |
| Sistema (iQFleet) | Valida credenciales, emite y verifica tokens, gestiona sesiones |

---

## 3. Criterios de Aceptación

### CA-005-1: Interfaz de inicio de sesión
1. El sistema debe ofrecer una interfaz de inicio de sesión para usuarios registrados.

### CA-005-2: Credenciales de acceso
2. El sistema debe permitir el ingreso mediante:
   - Correo electrónico
   - Contraseña

### CA-005-3: Validación de campos
3. El sistema debe validar que los campos obligatorios se encuentren diligenciados antes de procesar el inicio de sesión.

### CA-005-4: Verificación de cuenta
4. El sistema debe verificar que el correo electrónico exista y esté asociado a una cuenta activa.
5. El sistema debe validar que la contraseña ingresada coincida con la registrada en el sistema.
6. El sistema debe permitir el acceso únicamente cuando las credenciales sean válidas.

### CA-005-5: Mensajes de error
7. El sistema debe mostrar mensajes de error cuando:
   - El correo electrónico no exista
   - La contraseña sea incorrecta
   - La cuenta se encuentre inactiva o bloqueada

### CA-005-6: Control de acceso por rol
8. El sistema debe restringir el acceso a módulos, funcionalidades y opciones según el rol asignado al usuario.

### CA-005-7: Gestión de sesión
9. El sistema debe mantener una sesión activa mientras el usuario se encuentre utilizando la plataforma.
10. El sistema debe cerrar automáticamente la sesión después de un periodo de inactividad definido por la plataforma.
11. El sistema debe permitir al usuario cerrar sesión manualmente en cualquier momento.

### CA-005-8: Redirección post-login
12. El sistema debe redirigir al usuario a la pantalla principal correspondiente a su rol después de iniciar sesión con éxito.

---

## 4. Roles y matriz de permisos

| Módulo / Funcionalidad | OWNER | ADMIN |
|------------------------|-------|-------|
| Gestión de conductores | Completo | Completo |
| Gestión de vehículos | Completo | Completo |
| Gestión documental | Completo | Completo |
| Gestión financiera | Completo | Registrar y consultar |
| Generación de reportes | Completo | Consultar |
| Gestión de usuarios | Completo | Sin acceso |
| Panel administrativo | Completo | Consulta limitada |
| Vista de métricas | Completo | Completo |
| Configuración del sistema | Completo | Sin acceso |

---

## 5. Reglas de negocio

- Las contraseñas se almacenan únicamente como hash BCrypt (factor de costo ≥ 10). Nunca en texto plano.
- Después de 5 intentos fallidos consecutivos, la cuenta se bloquea temporalmente (30 minutos).
- El token JWT tiene un tiempo de expiración configurable (default: 8 horas).
- El token incluye el rol del usuario para autorizar sin consultar la base de datos en cada petición.
- El endpoint `/api/auth/login` es el único accesible sin autenticación previa.
- Una cuenta bloqueada solo puede ser desbloqueada por un usuario con rol `ROLE_OWNER`.

---

## 6. Estructura del JWT

```json
{
  "sub": "usuario@email.com",
  "rol": "ROLE_OWNER",
  "userId": 1,
  "iat": 1716000000,
  "exp": 1716028800
}
```

---

## 7. Configuración de Spring Security

```
SecurityFilterChain:
  - Deshabilitar CSRF (API REST stateless)
  - Stateless session management
  - JwtAuthenticationFilter ANTES de UsernamePasswordAuthenticationFilter
  - Rutas públicas: POST /api/auth/login, POST /api/auth/recuperar-password
  - Todas las demás rutas: authenticated()
  - Control por rol: hasRole("OWNER") para endpoints de gestión de usuarios
```

---

## 8. Modelo de datos

```
Usuario
├── id                   Long            PK · autogenerado
├── nombres              VARCHAR(100)    NOT NULL
├── apellidos            VARCHAR(100)    NOT NULL
├── tipoIdentificacion   VARCHAR(3)      NOT NULL  (CC, CE, PA)
├── numeroIdentificacion VARCHAR(12)     UNIQUE · NOT NULL
├── email                VARCHAR(150)    UNIQUE · NOT NULL
├── telefono             VARCHAR(15)
├── nombreUsuario        VARCHAR(50)     UNIQUE · NOT NULL
├── passwordHash         VARCHAR(255)    NOT NULL  (BCrypt)
├── rol                  ENUM            NOT NULL  (ROLE_OWNER, ROLE_ADMIN)
├── estado               ENUM            NOT NULL  default PENDIENTE
│                         (PENDIENTE, ACTIVO, INACTIVO, BLOQUEADO)
├── intentosFallidos     INTEGER         NOT NULL  default 0
├── bloqueadoHasta       TIMESTAMP       nullable
├── ultimoAcceso         TIMESTAMP       nullable
├── fechaCreacion        TIMESTAMP       NOT NULL
└── fechaModificacion    TIMESTAMP
```

---

## 9. Endpoints API REST

| Método | Ruta | Descripción | Código | Autenticación |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/auth/login` | Autenticación; retorna JWT | 200 | No requerida |
| `POST` | `/api/auth/logout` | Cierre de sesión | 200 | Requerida |
| `GET` | `/api/auth/me` | Perfil del usuario autenticado | 200 | Requerida |

---

## 10. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `LoginCommand` | Valida credenciales y emite JWT |
| `LogoutCommand` | Invalida la sesión activa |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetUsuarioByEmailQuery` | Busca usuario por email para validación |
| `GetPerfilUsuarioQuery` | Retorna datos del usuario autenticado |
