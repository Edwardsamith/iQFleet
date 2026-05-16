# RNF — Requerimientos No Funcionales

| Campo | Detalle |
|-------|---------|
| **ID** | RNF-001 a RNF-006 |
| **Tipo** | No funcional |
| **Prioridad** | Alta (aplican transversalmente a todos los módulos) |
| **Estado** | Definidos |

---

## RNF-001: Seguridad

### RNF-001.1 — Autenticación obligatoria
Todos los endpoints del sistema, excepto `/api/auth/login` y `/api/auth/registro`, deben requerir un token JWT válido en el encabezado `Authorization: Bearer {token}`.

**Criterio de verificación:** 0 endpoints funcionales accesibles sin autenticación (validado con pruebas de integración).

### RNF-001.2 — Cifrado de contraseñas
Las contraseñas de usuarios se almacenan exclusivamente como hash BCrypt con factor de costo ≥ 10. Ninguna contraseña en texto plano puede existir en la base de datos ni en los logs.

**Criterio de verificación:** Revisión de código + auditoría de base de datos.

### RNF-001.3 — Autorización por roles
Cada endpoint debe validar que el rol del usuario sea el requerido. Un usuario con `ROLE_ADMIN` que intente acceder a un endpoint exclusivo de `ROLE_OWNER` debe recibir un error HTTP 403.

**Criterio de verificación:** Pruebas de autorización por cada endpoint sensible.

### RNF-001.4 — Protección contra inyección SQL
El sistema debe usar consultas parametrizadas (JPA/Hibernate) para todo acceso a datos. Está prohibida la concatenación directa de variables en consultas HQL/JPQL/SQL nativo.

### RNF-001.5 — Validación de entrada
Todos los datos recibidos por la API deben ser validados mediante Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email`, etc.) antes de procesarse.

### RNF-001.6 — Protección de información sensible
Los mensajes de error expuestos al cliente no deben revelar detalles internos del sistema (stack traces, nombres de tablas, estructura de la base de datos). En producción, los errores 500 deben retornar un mensaje genérico.

### RNF-001.7 — Bloqueo por intentos fallidos
Después de 5 intentos fallidos de inicio de sesión consecutivos, la cuenta se bloquea por 30 minutos. Solo un usuario `ROLE_OWNER` puede desbloquearla antes del tiempo.

---

## RNF-002: Rendimiento

### RNF-002.1 — Tiempo de respuesta
El tiempo de respuesta de los endpoints de consulta debe ser inferior a **500 ms** para conjuntos de hasta 1.000 registros en condiciones normales de carga.

### RNF-002.2 — Paginación obligatoria
Todos los endpoints que retornen listas de registros deben implementar paginación. Está prohibido retornar colecciones sin límite.

**Parámetros estándar:**
- `page`: número de página (default: 0)
- `size`: registros por página (default: 20, máximo: 100)

### RNF-002.3 — Índices de base de datos
Las columnas usadas frecuentemente como filtro de búsqueda deben tener índice en la base de datos:
- `Conductor.numeroIdentificacion`
- `Conductor.vencimientoLicencia`
- `Vehiculo.placa`
- `Documento.fechaVencimiento`
- `Documento.estado`
- `MovimientoFinanciero.fecha`
- `Usuario.email`
- `Usuario.nombreUsuario`

### RNF-002.4 — Carga lazy en relaciones
Las relaciones `@OneToMany` deben configurarse con `FetchType.LAZY` para evitar la carga innecesaria de colecciones grandes al consultar una entidad.

---

## RNF-003: Usabilidad de la API

### RNF-003.1 — Documentación automática con OpenAPI
La API debe estar completamente documentada con OpenAPI 3.0 mediante SpringDoc. La documentación debe ser accesible en `/swagger-ui.html` en entornos de desarrollo y QA.

### RNF-003.2 — Formato uniforme de respuestas de éxito
Las respuestas exitosas de creación deben incluir el recurso creado con su ID asignado y código HTTP 201. Las consultas retornan HTTP 200.

### RNF-003.3 — Formato uniforme de errores
Todos los errores deben seguir el siguiente formato JSON:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción legible del error",
  "details": ["campo: regla incumplida"],
  "timestamp": "2026-05-14T10:00:00Z",
  "path": "/api/conductores"
}
```

### RNF-003.4 — Códigos HTTP semánticos
El sistema debe retornar los códigos HTTP que correspondan a cada situación:

| Situación | Código HTTP |
|-----------|-------------|
| Creación exitosa | 201 Created |
| Consulta exitosa | 200 OK |
| Sin contenido | 204 No Content |
| Campo inválido | 400 Bad Request |
| No autenticado | 401 Unauthorized |
| Sin permisos | 403 Forbidden |
| No encontrado | 404 Not Found |
| Conflicto (duplicado) | 409 Conflict |
| Error interno | 500 Internal Server Error |

---

## RNF-004: Disponibilidad y Confiabilidad

### RNF-004.1 — Disponibilidad objetivo
El sistema debe tener una disponibilidad mínima del **99 %** en el entorno de producción.

### RNF-004.2 — Consistencia transaccional
Las operaciones que modifiquen múltiples registros de forma atómica deben ejecutarse dentro de una transacción (`@Transactional`). Si una parte falla, toda la operación debe revertirse.

**Ejemplos de operaciones transaccionales:**
- Asignar conductor a vehículo (actualiza Conductor + HistorialAsignacion).
- Cambiar estado de vehículo a EN_MANTENIMIENTO (actualiza Vehiculo + desasigna Conductor).
- Aprobar solicitud de usuario (actualiza SolicitudRegistro + activa Usuario).

### RNF-004.3 — Manejo de errores en producción
En producción el sistema nunca debe exponer stack traces, nombres de clases internas ni mensajes de excepción en bruto en las respuestas HTTP.

### RNF-004.4 — Integridad referencial
Las claves foráneas deben estar definidas en el esquema de base de datos. No se puede eliminar un registro que sea referenciado por otros (ej. no eliminar un Conductor que tenga documentos; la operación debe ser rechazada con HTTP 409).

---

## RNF-005: Mantenibilidad

### RNF-005.1 — Separación estricta de capas (Clean Architecture)
El código debe respetar la siguiente regla de dependencia sin excepciones:

```
Presentation → Application → Domain ← Infrastructure
```

- `Domain` no debe importar clases de Spring, Hibernate ni ninguna otra librería externa.
- `Application` no debe importar clases de `Infrastructure`.
- `Presentation` no debe acceder directamente a repositorios.

### RNF-005.2 — Un handler por Command/Query
Cada `Command` y cada `Query` debe tener exactamente un `Handler`. Los Handlers no deben llamarse entre sí; si necesitan funcionalidad compartida, deben extraerla a un servicio de dominio o a un repositorio.

### RNF-005.3 — Cobertura de pruebas
Los `Handler` de la capa `Application` deben tener cobertura de pruebas unitarias mínima del **80 %**. Las pruebas deben usar implementaciones en memoria de los repositorios (no mocks de Mockito para los repositorios principales).

### RNF-005.4 — Convenciones de nomenclatura
El proyecto sigue las convenciones estándar de Java:
- Clases: `PascalCase`
- Métodos y variables: `camelCase`
- Constantes: `UPPER_SNAKE_CASE`
- Paquetes: `lowercase`
- Nombres de Commands: verbo + entidad + "Command" (ej. `CreateConductorCommand`)
- Nombres de Queries: "Get" + entidad + criterio + "Query" (ej. `GetConductorByIdQuery`)

---

## RNF-006: Portabilidad y Configuración

### RNF-006.1 — Configuración por entorno
Las propiedades sensibles deben externalizarse y nunca estar en código fuente:
- Credenciales de base de datos
- Secret del JWT y tiempo de expiración
- Configuración de correo/SMS para OTP
- Período de alerta de vencimiento de documentos (default: 30 días)

Usar perfiles de Spring: `application-dev.properties` y `application-prod.properties`.

### RNF-006.2 — Empaquetado ejecutable
El proyecto debe poder empaquetarse con `mvn package` y ejecutarse con `java -jar iQFleet.jar` sin dependencias externas además de la JVM y la base de datos.

### RNF-006.3 — Base de datos configurable
La capa de persistencia debe funcionar con cualquier base de datos relacional compatible con Hibernate (MySQL, PostgreSQL, H2 para pruebas) cambiando solo la configuración del `application.properties`.
