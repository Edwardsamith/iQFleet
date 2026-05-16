# RF-010 — Generación de Reportes

| Campo | Detalle |
|-------|---------|
| **ID** | RF-010 |
| **Módulo** | Generación de Reportes |
| **Prioridad** | Media-Alta |
| **Estado** | Pendiente de implementación |
| **Depende de** | RF-001, RF-002, RF-003, RF-004, RF-005 |
| **Relacionado con** | RF-009 (panel de métricas — usa datos similares) |

---

## 1. Descripción

El sistema debe permitir generar reportes financieros, documentales y operativos basados en la información histórica almacenada en la plataforma. Los reportes deben poder filtrarse según criterios definidos por el usuario y exportarse para análisis, auditoría y seguimiento administrativo.

---

## 2. Actores

| Actor | Permisos |
|-------|----------|
| Propietario (`ROLE_OWNER`) | Acceso completo: generar y exportar todos los tipos de reportes |
| Administrador (`ROLE_ADMIN`) | Generar y consultar reportes; exportación según configuración |

---

## 3. Criterios de Aceptación

### CA-010-1: Acceso al módulo
1. El sistema debe permitir generar reportes para usuarios autorizados.

### CA-010-2: Filtros de generación
2. El sistema debe permitir generar reportes mediante filtros definidos por el usuario.
3. El sistema debe permitir filtrar reportes por:
   - Fechas
   - Tipo de reporte
   - Categoría
   - Estado de registros
   - Vehículo o conductor asociado

### CA-010-3: Información financiera en reportes
4. El sistema debe permitir incluir información financiera dentro de los reportes, como:
   - Ingresos
   - Egresos
   - Balances
   - Movimientos financieros
   - Categorías financieras

### CA-010-4: Información documental en reportes
5. El sistema debe permitir incluir información documental dentro de los reportes, como:
   - Tipo de documento
   - Estado de vigencia
   - Fechas de vencimiento
   - Documentos próximos a vencer
   - Documentos vencidos

### CA-010-5: Información operativa en reportes
6. El sistema debe permitir incluir información operativa relacionada con:
   - Vehículos registrados
   - Conductores registrados
   - Indicadores generales de operación

### CA-010-6: Reportes históricos
7. El sistema debe generar reportes históricos para procesos de análisis, auditoría y seguimiento administrativo.

### CA-010-7: Contenido de los reportes
8. El sistema debe mostrar información resumida y detallada dentro de los reportes generados.

### CA-010-8: Exportación
9. El sistema debe permitir exportar los reportes generados.
10. El sistema debe garantizar la integridad de la información exportada en los reportes.

### CA-010-9: Control de acceso
11. El sistema debe restringir el acceso a la generación y exportación de reportes únicamente a usuarios con permisos autorizados.

---

## 4. Tipos de reportes

### Reportes financieros

| Nombre | Descripción | Filtros clave |
|--------|-------------|---------------|
| Balance por vehículo | Ingresos, egresos y utilidad neta de una unidad | Vehículo, período |
| Balance consolidado de flota | Resumen financiero de toda la flota | Período |
| Movimientos financieros | Listado detallado de todos los movimientos | Tipo, categoría, método de pago, vehículo, período |
| Rentabilidad comparativa | Comparación de rentabilidad entre vehículos | Período |
| Egresos por categoría | Desglose de gastos por tipo | Categoría, vehículo, período |

### Reportes documentales

| Nombre | Descripción | Filtros clave |
|--------|-------------|---------------|
| Estado documental de la flota | Documentos de todos los vehículos con su estado de vigencia | Estado, tipo |
| Estado documental de conductores | Documentos de todos los conductores | Estado, tipo |
| Documentos próximos a vencer | Documentos que vencen en los próximos N días | Días, tipo |
| Documentos vencidos | Todos los documentos con estado VENCIDO | Tipo, entidad |
| Historial de renovaciones | Registro de todas las renovaciones documentales | Período, tipo |

### Reportes operativos

| Nombre | Descripción | Filtros clave |
|--------|-------------|---------------|
| Inventario de flota | Listado de todos los vehículos con su estado | Estado, tipo |
| Listado de conductores | Conductores con su estado y asignación | Estado, categoría de licencia |
| Historial de asignaciones | Registro de asignaciones conductor-vehículo | Vehículo, conductor, período |
| Registro de mantenimientos | Historial de entradas y salidas de mantenimiento | Vehículo, período |

---

## 5. Formatos de exportación

| Formato | Extensión | Descripción |
|---------|-----------|-------------|
| PDF | `.pdf` | Reporte formateado para presentación e impresión |
| Excel | `.xlsx` | Datos tabulados para análisis en hojas de cálculo |
| CSV | `.csv` | Datos planos para importación en otras herramientas |

---

## 6. Reglas de negocio

- Los reportes se generan en tiempo real sobre la información almacenada en el momento de la solicitud.
- No se almacenan copias de los reportes generados; cada exportación genera el reporte de nuevo.
- El rango de fechas máximo para un reporte es de 12 meses en una sola solicitud.
- Los reportes exportados incluyen: nombre del sistema, fecha de generación, usuario que lo generó y los filtros aplicados.
- Un reporte que no arroje resultados debe indicarlo explícitamente, no retornar un archivo vacío sin contexto.

---

## 7. Estructura del reporte exportado

Todo reporte exportado debe incluir el siguiente encabezado:

```
Sistema: iQFleet
Título del reporte: [nombre del reporte]
Generado por: [nombre del usuario]
Fecha de generación: [dd/mm/yyyy HH:mm]
Filtros aplicados: [descripción de los filtros]
Período: [fecha desde] – [fecha hasta]
─────────────────────────────────────────
[contenido del reporte]
─────────────────────────────────────────
Total de registros: [n]
```

---

## 8. Endpoints API REST

| Método | Ruta | Descripción | Código | Rol requerido |
|--------|------|-------------|--------|---------------|
| `GET` | `/api/reportes/financiero/balance-vehiculo` | Reporte de balance por vehículo | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/financiero/consolidado` | Balance consolidado de la flota | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/financiero/movimientos` | Listado de movimientos financieros | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/documental/estado-flota` | Estado documental de vehículos | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/documental/estado-conductores` | Estado documental de conductores | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/documental/vencimientos` | Documentos por vencer o vencidos | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/operativo/inventario` | Inventario de flota | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/operativo/conductores` | Listado de conductores | 200 | OWNER, ADMIN |
| `GET` | `/api/reportes/exportar` | Exporta cualquier reporte en el formato solicitado | 200 | OWNER, ADMIN |

**Parámetros comunes:**

| Parámetro | Descripción |
|-----------|-------------|
| `tipo` | Tipo de reporte (ver tabla de tipos) |
| `fechaDesde` / `fechaHasta` | Rango de fechas |
| `vehiculoId` | Filtrar por vehículo |
| `conductorId` | Filtrar por conductor |
| `estado` | Estado de los registros |
| `formato` | PDF \| XLSX \| CSV (solo para exportar) |

---

## 9. Queries (CQRS)

| Clase | Descripción |
|-------|-------------|
| `GetReporteFinancieroQuery` | Genera el reporte financiero con los filtros dados |
| `GetReporteDocumentalQuery` | Genera el reporte documental con los filtros dados |
| `GetReporteOperativoQuery` | Genera el reporte operativo con los filtros dados |
| `ExportarReporteQuery` | Genera el archivo exportable en el formato solicitado |
