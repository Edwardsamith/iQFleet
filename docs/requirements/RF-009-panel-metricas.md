# RF-009 — Panel Administrativo y Vista de Métricas

| Campo | Detalle |
|-------|---------|
| **ID** | RF-009 |
| **Módulo** | Panel Administrativo / Vista de Métricas |
| **Prioridad** | Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-001, RF-002, RF-003, RF-004, RF-005, RF-008 |
| **Relacionado con** | RF-010 (reportes) |

---

## 1. Descripción

Este módulo agrupa dos vistas complementarias:

1. **Panel administrativo:** orientado al equipo de soporte o administración de la plataforma; muestra el estado operativo general de los usuarios y la actividad del sistema.
2. **Vista de métricas operativas y financieras:** orientada al propietario; muestra indicadores de la flota, balances financieros, alertas activas y totalizadores operativos.

Ambas vistas consolidan información de todos los demás módulos y la presentan de manera resumida para facilitar la toma de decisiones.

---

## 2. Actores

| Actor | Acceso |
|-------|--------|
| Propietario (`ROLE_OWNER`) | Panel administrativo completo + Vista de métricas completa |
| Administrador (`ROLE_ADMIN`) | Vista de métricas completa; panel administrativo con consulta limitada |

---

## 3. Criterios de Aceptación — Panel Administrativo

### CA-009-1: Acceso al panel
1. El sistema debe ofrecer un panel administrativo para el equipo de soporte.

### CA-009-2: Información general del estado
2. El sistema debe mostrar información general relacionada con:
   - Cantidad total de usuarios registrados
   - Usuarios activos
   - Usuarios inactivos
   - Solicitudes pendientes
   - Actividad reciente dentro de la plataforma

### CA-009-3: Estadísticas descriptivas
3. El sistema debe mostrar estadísticas descriptivas del uso de la plataforma mediante tablas, gráficos e indicadores.

### CA-009-4: Métricas de uso
4. El sistema debe permitir consultar métricas relacionadas con:
   - Inicio de sesiones
   - Frecuencia de uso
   - Accesos recientes
   - Actividad de usuarios

### CA-009-5: Estado de usuarios
5. El sistema debe identificar y reflejar el estado actual de los usuarios: **Activo** / **Inactivo**.

### CA-009-6: Actualización de datos
6. El sistema debe actualizar la información del panel en tiempo real o mediante actualizaciones periódicas.

### CA-009-7: Filtros del panel
7. El sistema debe permitir filtrar o consultar información según:
   - Fecha
   - Tipo de usuario
   - Estado del usuario

### CA-009-8: Control de acceso del panel
8. El sistema debe restringir el acceso al panel administrativo únicamente a usuarios con rol administrativo.
9. El sistema debe mantener la integridad y confidencialidad de la información visualizada dentro del panel administrativo.

---

## 4. Criterios de Aceptación — Vista de Métricas Operativas y Financieras

### CA-009-9: Panel de métricas
1. El sistema debe mostrar un panel de métricas operativas y financieras para los usuarios.

### CA-009-10: Movimientos financieros
2. El sistema debe visualizar los ingresos y egresos registrados, incluyendo:
   - Valor
   - Fecha de registro
   - Concepto asociado
   - Estado del movimiento financiero

### CA-009-11: Balances financieros
3. El sistema debe calcular y mostrar balances financieros generales basados en los ingresos y egresos registrados.

### CA-009-12: Indicadores financieros
4. El sistema debe mostrar los siguientes indicadores financieros:
   - Total de ingresos
   - Total de egresos
   - Balance general
   - Movimientos recientes

### CA-009-13: Totalizadores operativos
5. El sistema debe mostrar totalizadores operativos relacionados con:
   - Cantidad total de vehículos registrados
   - Cantidad total de conductores registrados
   - Cantidad total de documentos registrados
   - Vehículos activos e inactivos
   - Conductores activos e inactivos

### CA-009-14: Alertas y recordatorios
6. El sistema debe visualizar alertas y recordatorios pendientes relacionados con:
   - Documentos próximos a vencer
   - Mantenimientos pendientes
   - Pagos pendientes
   - Novedades operativas registradas

### CA-009-15: Filtros de métricas
7. El sistema debe permitir filtrar la información visualizada según:
   - Fechas
   - Tipo de indicador
   - Estado de registros
   - Categoría operativa o financiera

### CA-009-16: Actualización automática
8. El sistema debe actualizar automáticamente la información mostrada en el panel.

---

## 5. Indicadores clave del panel (KPIs)

### Indicadores financieros

| Indicador | Descripción | Cálculo |
|-----------|-------------|---------|
| Total ingresos del período | Suma de ingresos en el rango de fechas | `ΣMovimientos tipo INGRESO` |
| Total egresos del período | Suma de egresos en el rango de fechas | `ΣMovimientos tipo EGRESO` |
| Balance general | Diferencia neta del período | `Ingresos − Egresos` |
| Vehículo más rentable | Unidad con mayor utilidad neta | Top 1 por balance |
| Vehículo con mayores costos | Unidad con mayor egreso | Top 1 por total egresos |

### Indicadores operativos

| Indicador | Descripción |
|-----------|-------------|
| Total vehículos | Conteo de todos los vehículos registrados |
| Vehículos activos | Vehículos con estado ACTIVO |
| Vehículos en mantenimiento | Vehículos con estado EN_MANTENIMIENTO |
| Vehículos inactivos | Vehículos con estado INACTIVO |
| Total conductores | Conteo de todos los conductores registrados |
| Conductores activos | Conductores con estado ACTIVO |
| Conductores inactivos | Conductores con estado INACTIVO |
| Total documentos | Conteo de todos los documentos registrados |

### Indicadores de alertas

| Indicador | Descripción |
|-----------|-------------|
| Documentos próximos a vencer | Documentos que vencen en los próximos 30 días |
| Documentos vencidos | Documentos con fecha de vencimiento superada |
| Conductores con licencia próxima a vencer | Conductores cuya licencia vence en 30 días o menos |

---

## 6. Reglas de negocio

- Los indicadores del panel se calculan al momento de la consulta (sin almacenamiento intermedio).
- El período de análisis por defecto es el mes en curso; el usuario puede modificarlo mediante filtros.
- Las alertas de documentos usan la misma lógica de cálculo de estado que RF-003.
- El panel administrativo solo muestra datos de usuarios, no de flota; la vista de métricas muestra datos de flota.

---

## 7. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `GET` | `/api/dashboard/resumen-flota` | Totalizadores operativos de la flota | 200 | OWNER, ADMIN |
| `GET` | `/api/dashboard/resumen-financiero` | Indicadores financieros del período | 200 | OWNER, ADMIN |
| `GET` | `/api/dashboard/alertas` | Alertas activas (documentos, mantenimientos) | 200 | OWNER, ADMIN |
| `GET` | `/api/dashboard/panel-admin` | Estado de usuarios y actividad del sistema | 200 | OWNER, ADMIN |

**Parámetros de filtro:**

| Parámetro | Descripción |
|-----------|-------------|
| `fechaDesde` / `fechaHasta` | Rango de fechas para los indicadores financieros |
| `categoriaIndicador` | Filtro por tipo de indicador (financiero, operativo, alertas) |

---

## 8. Queries (CQRS)

| Clase | Descripción |
|-------|-------------|
| `GetResumenFlotaQuery` | Totalizadores operativos (vehículos, conductores, documentos) |
| `GetResumenFinancieroQuery` | Balance general + indicadores financieros del período |
| `GetAlertasActivasQuery` | Documentos vencidos y próximos a vencer |
| `GetPanelAdminQuery` | Estado de usuarios + métricas de acceso |
