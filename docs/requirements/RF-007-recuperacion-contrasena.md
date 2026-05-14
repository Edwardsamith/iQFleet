# RF-007 — Recuperación de Contraseña

| Campo | Detalle |
|-------|---------|
| **ID** | RF-007 |
| **Módulo** | Recuperación de Contraseña |
| **Prioridad** | Media |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-005 (autenticación), RF-006 (registro de usuarios) |
| **Relacionado con** | — |

---

## 1. Descripción

El sistema debe permitir al usuario recuperar el acceso a su cuenta mediante la verificación de identidad utilizando el correo electrónico o número telefónico registrado. El proceso utiliza un código de verificación único y temporal para garantizar que solo el propietario de la cuenta pueda restablecer su contraseña.

---

## 2. Actores

| Actor | Rol |
|-------|-----|
| Usuario registrado | Solicita la recuperación de contraseña |
| Sistema (iQFleet) | Genera, envía y valida el código de verificación |

---

## 3. Criterios de Aceptación

### CA-007-1: Opción de recuperación
1. El sistema debe ofrecer una opción de "Recuperar contraseña" en la pantalla de inicio de sesión.

### CA-007-2: Solicitud de medio de contacto
2. El sistema debe solicitar al usuario un correo electrónico o número telefónico que esté registrado anteriormente.

### CA-007-3: Validación de existencia
3. El sistema debe validar la existencia del correo o número ingresado.

### CA-007-4: Generación del código
4. El sistema debe generar un código de verificación único y temporal.
5. El código de verificación debe tener un tiempo de vigencia establecido.

### CA-007-5: Envío del código
6. El sistema debe enviar el código de verificación al correo electrónico o número telefónico proporcionado.

### CA-007-6: Ingreso del código
7. El sistema debe solicitar al usuario el ingreso del código recibido.

### CA-007-7: Validación del código
8. El sistema debe validar:
   - La coincidencia del código ingresado
   - La vigencia del código
   - Que el código no haya sido utilizado previamente

### CA-007-8: Restablecimiento de contraseña
9. Si la validación es exitosa, el sistema debe permitir al usuario establecer una nueva contraseña.
10. El sistema debe confirmar que la contraseña fue actualizada correctamente.

### CA-007-9: Manejo de errores
11. Si el código es inválido o expiró, el sistema debe mostrar un mensaje de error y permitir solicitar un nuevo código.

---

## 4. Flujo del proceso

```
[Usuario]  Accede a "Recuperar contraseña"
           │
           ▼
[Sistema]  Solicita correo o teléfono registrado
           │
           ▼
[Sistema]  Valida existencia del correo / teléfono
           │ no existe → Error: "No existe cuenta asociada a ese dato"
           │ existe
           ▼
[Sistema]  Genera código OTP (6 dígitos) único y temporal
           Vigencia: 15 minutos
           Marca código como NO_USADO
           │
           ▼
[Sistema]  Envía código por correo o SMS
           │
           ▼
[Usuario]  Ingresa el código recibido
           │
[Sistema]  Valida: coincidencia + vigencia + no usado
           │ inválido / expirado → Error + opción de reenviar
           │ válido
           ▼
[Sistema]  Habilita formulario de nueva contraseña
           │
           ▼
[Usuario]  Ingresa y confirma nueva contraseña
           │
[Sistema]  Valida requisitos de la contraseña
           Actualiza hash BCrypt
           Marca código como USADO
           │
           ▼
[Sistema]  Confirma actualización exitosa
           Redirige a inicio de sesión
```

---

## 5. Reglas de negocio

- El código OTP tiene vigencia de 15 minutos desde su generación.
- Un código solo puede usarse una vez; después queda marcado como `USADO`.
- Solo puede haber un código activo por usuario a la vez. Solicitar uno nuevo invalida el anterior.
- Si el correo no existe en el sistema, el mensaje de error no debe confirmar ni negar su existencia (prevención de enumeración de usuarios).
- La nueva contraseña debe cumplir los mismos requisitos que en el registro: mínimo 8 caracteres, al menos 1 mayúscula y 1 número.
- Después del restablecimiento, todas las sesiones activas del usuario deben invalidarse.

---

## 6. Modelo de datos

```
CodigoRecuperacion
├── id                   Long            PK · autogenerado
├── usuario              FK Usuario      NOT NULL
├── codigo               VARCHAR(6)      NOT NULL  (hash del código OTP)
├── medio                ENUM            NOT NULL  (EMAIL, SMS)
├── destinatario         VARCHAR(150)    NOT NULL  (correo o teléfono enmascarado en logs)
├── estado               ENUM            NOT NULL  default PENDIENTE
│                         (PENDIENTE, USADO, EXPIRADO)
├── fechaExpiracion      TIMESTAMP       NOT NULL
├── fechaUso             TIMESTAMP       nullable
└── fechaCreacion        TIMESTAMP       NOT NULL
```

---

## 7. Endpoints API REST

| Método | Ruta | Descripción | Código | Autenticación |
|--------|------|-------------|--------|---------------|
| `POST` | `/api/auth/recuperar-password` | Solicitar código de recuperación | 200 | No requerida |
| `POST` | `/api/auth/verificar-codigo` | Validar código OTP | 200 | No requerida |
| `POST` | `/api/auth/restablecer-password` | Establecer nueva contraseña | 200 | No requerida (requiere token temporal) |

---

## 8. Comandos y Queries (CQRS)

### Commands

| Clase | Descripción |
|-------|-------------|
| `SolicitarRecuperacionCommand` | Genera y envía el código OTP |
| `VerificarCodigoRecuperacionCommand` | Valida el código; retorna token temporal |
| `RestablecerPasswordCommand` | Actualiza la contraseña con el token temporal |

### Queries

| Clase | Descripción |
|-------|-------------|
| `GetCodigoActivoByUsuarioQuery` | Verifica si ya existe un código activo para el usuario |
