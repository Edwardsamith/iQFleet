# Diagrama de Casos de Uso — iQFleet

---

## Actores del sistema

| Actor | Tipo | Descripción |
|-------|------|-------------|
| **Propietario** | Principal | Dueño de la flota. Acceso completo al sistema |
| **Administrador** | Principal | Usuario delegado. Acceso operativo sin gestión de usuarios ni configuración |
| **Usuario Solicitante** | Secundario | Persona que solicita acceso; no está autenticada aún |
| **Sistema (iQFleet)** | Secundario | Procesa lógica de negocio, genera alertas y calcula métricas |

---

## Diagrama de casos de uso (representación textual)

```
═══════════════════════════════════════════════════════════════
                        SISTEMA iQFleet
═══════════════════════════════════════════════════════════════

ACTOR: Usuario Solicitante
  ├── (CU-06-1) Solicitar acceso / Registrarse
  └── (CU-07-1) Recuperar contraseña

───────────────────────────────────────────────────────────────

ACTOR: Propietario + Administrador (compartidos)
  │
  ├── MÓDULO: Autenticación
  │     ├── (CU-05-1) Iniciar sesión
  │     ├── (CU-05-2) Cerrar sesión
  │     └── (CU-05-3) Ver perfil propio
  │
  ├── MÓDULO: Conductores
  │     ├── (CU-01-1) Registrar conductor
  │     ├── (CU-01-2) Consultar conductores (con filtros)
  │     ├── (CU-01-3) Consultar conductor por ID
  │     ├── (CU-01-4) Actualizar datos del conductor
  │     ├── (CU-01-5) Activar / Desactivar conductor
  │     └── (CU-01-6) Ver alertas de licencias próximas a vencer
  │
  ├── MÓDULO: Vehículos
  │     ├── (CU-02-1) Registrar vehículo
  │     ├── (CU-02-2) Consultar vehículos (con filtros)
  │     ├── (CU-02-3) Consultar vehículo por ID
  │     ├── (CU-02-4) Actualizar datos del vehículo
  │     ├── (CU-02-5) Cambiar estado del vehículo (Activo/Mantenimiento/Inactivo)
  │     ├── (CU-02-6) Asignar conductor a vehículo
  │     ├── (CU-02-7) Desasignar conductor
  │     └── (CU-02-8) Ver historial de asignaciones
  │
  ├── MÓDULO: Gestión Documental
  │     ├── (CU-03-1) Cargar documento (conductor o vehículo)
  │     ├── (CU-03-2) Consultar documentos por conductor
  │     ├── (CU-03-3) Consultar documentos por vehículo
  │     ├── (CU-03-4) Renovar / reemplazar documento
  │     ├── (CU-03-5) Ver documentos próximos a vencer
  │     ├── (CU-03-6) Ver documentos vencidos
  │     └── (CU-03-7) Marcar documento como inactivo
  │
  ├── MÓDULO: Gestión Financiera
  │     ├── (CU-04-1) Registrar ingreso
  │     ├── (CU-04-2) Registrar egreso
  │     ├── (CU-04-3) Consultar movimientos (con filtros)
  │     ├── (CU-04-4) Ver balance por vehículo
  │     ├── (CU-04-5) Ver balance consolidado de la flota
  │     └── (CU-04-6) Anular movimiento financiero
  │
  ├── MÓDULO: Métricas y Panel
  │     ├── (CU-09-1) Ver resumen operativo de la flota
  │     ├── (CU-09-2) Ver indicadores financieros del período
  │     ├── (CU-09-3) Ver alertas activas (documentos, mantenimientos)
  │     └── (CU-09-4) Filtrar información del panel
  │
  └── MÓDULO: Reportes
        ├── (CU-10-1) Generar reporte financiero
        ├── (CU-10-2) Generar reporte documental
        ├── (CU-10-3) Generar reporte operativo
        └── (CU-10-4) Exportar reporte (PDF / XLSX / CSV)

───────────────────────────────────────────────────────────────

ACTOR: Solo Propietario (ROLE_OWNER)
  │
  ├── MÓDULO: Gestión de Usuarios
  │     ├── (CU-08-1) Crear usuario
  │     ├── (CU-08-2) Consultar usuarios (con filtros)
  │     ├── (CU-08-3) Actualizar datos del usuario
  │     ├── (CU-08-4) Activar / Desactivar usuario
  │     └── (CU-08-5) Restablecer contraseña de usuario (forzado)
  │
  └── MÓDULO: Solicitudes de Acceso
        ├── (CU-06-2) Ver solicitudes de registro pendientes
        ├── (CU-06-3) Aprobar solicitud de registro
        └── (CU-06-4) Rechazar solicitud de registro

───────────────────────────────────────────────────────────────

ACTOR: Sistema (iQFleet) — Comportamientos automáticos
  ├── Calcular estado de vigencia de documentos
  ├── Generar alertas de vencimiento
  ├── Calcular balances financieros
  ├── Bloquear cuenta después de 5 intentos fallidos
  ├── Generar código OTP para recuperación de contraseña
  └── Desasignar conductor al cambiar vehículo a EN_MANTENIMIENTO

═══════════════════════════════════════════════════════════════
```

---

## Tabla de casos de uso con requerimientos

| CU | Descripción | RF asociado | Actor |
|----|-------------|-------------|-------|
| CU-01-1 | Registrar conductor | RF-001 | OWNER, ADMIN |
| CU-01-2 | Consultar conductores | RF-001 | OWNER, ADMIN |
| CU-01-3 | Consultar conductor por ID | RF-001 | OWNER, ADMIN |
| CU-01-4 | Actualizar conductor | RF-001 | OWNER, ADMIN |
| CU-01-5 | Activar / Desactivar conductor | RF-001 | OWNER, ADMIN |
| CU-01-6 | Ver alertas de licencias | RF-001, RF-009 | OWNER, ADMIN |
| CU-02-1 | Registrar vehículo | RF-002 | OWNER, ADMIN |
| CU-02-2 | Consultar vehículos | RF-002 | OWNER, ADMIN |
| CU-02-3 | Consultar vehículo por ID | RF-002 | OWNER, ADMIN |
| CU-02-4 | Actualizar vehículo | RF-002 | OWNER, ADMIN |
| CU-02-5 | Cambiar estado del vehículo | RF-002 | OWNER, ADMIN |
| CU-02-6 | Asignar conductor | RF-002 | OWNER, ADMIN |
| CU-02-7 | Desasignar conductor | RF-002 | OWNER, ADMIN |
| CU-02-8 | Ver historial de asignaciones | RF-002 | OWNER, ADMIN |
| CU-03-1 | Cargar documento | RF-003 | OWNER, ADMIN |
| CU-03-2 | Consultar documentos conductor | RF-003 | OWNER, ADMIN |
| CU-03-3 | Consultar documentos vehículo | RF-003 | OWNER, ADMIN |
| CU-03-4 | Renovar documento | RF-003 | OWNER, ADMIN |
| CU-03-5 | Ver documentos próximos a vencer | RF-003 | OWNER, ADMIN |
| CU-03-6 | Ver documentos vencidos | RF-003 | OWNER, ADMIN |
| CU-03-7 | Marcar documento inactivo | RF-003 | OWNER, ADMIN |
| CU-04-1 | Registrar ingreso | RF-004 | OWNER, ADMIN |
| CU-04-2 | Registrar egreso | RF-004 | OWNER, ADMIN |
| CU-04-3 | Consultar movimientos | RF-004 | OWNER, ADMIN |
| CU-04-4 | Ver balance por vehículo | RF-004 | OWNER, ADMIN |
| CU-04-5 | Ver balance consolidado | RF-004 | OWNER, ADMIN |
| CU-04-6 | Anular movimiento | RF-004 | OWNER |
| CU-05-1 | Iniciar sesión | RF-005 | Todos |
| CU-05-2 | Cerrar sesión | RF-005 | Todos |
| CU-05-3 | Ver perfil propio | RF-005 | Todos |
| CU-06-1 | Solicitar acceso | RF-006 | Solicitante |
| CU-06-2 | Ver solicitudes pendientes | RF-006 | OWNER, ADMIN |
| CU-06-3 | Aprobar solicitud | RF-006 | OWNER, ADMIN |
| CU-06-4 | Rechazar solicitud | RF-006 | OWNER, ADMIN |
| CU-07-1 | Solicitar recuperación de contraseña | RF-007 | Todos |
| CU-07-2 | Ingresar código OTP | RF-007 | Todos |
| CU-07-3 | Establecer nueva contraseña | RF-007 | Todos |
| CU-08-1 | Crear usuario | RF-008 | OWNER, ADMIN |
| CU-08-2 | Consultar usuarios | RF-008 | OWNER, ADMIN |
| CU-08-3 | Actualizar usuario | RF-008 | OWNER, ADMIN |
| CU-08-4 | Activar / Desactivar usuario | RF-008 | OWNER, ADMIN |
| CU-08-5 | Restablecer contraseña | RF-008 | OWNER |
| CU-09-1 | Ver resumen operativo | RF-009 | OWNER, ADMIN |
| CU-09-2 | Ver indicadores financieros | RF-009 | OWNER, ADMIN |
| CU-09-3 | Ver alertas activas | RF-009 | OWNER, ADMIN |
| CU-09-4 | Ver panel administrativo | RF-009 | OWNER, ADMIN |
| CU-10-1 | Generar reporte financiero | RF-010 | OWNER, ADMIN |
| CU-10-2 | Generar reporte documental | RF-010 | OWNER, ADMIN |
| CU-10-3 | Generar reporte operativo | RF-010 | OWNER, ADMIN |
| CU-10-4 | Exportar reporte | RF-010 | OWNER, ADMIN |
