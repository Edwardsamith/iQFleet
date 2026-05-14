# Descripción General del Proyecto — iQFleet

| Campo | Detalle |
|-------|---------|
| **Nombre del sistema** | iQFleet |
| **Institución** | Universidad Popular del Cesar |
| **Facultad** | Ingeniería |
| **Asignatura** | Programación de Computadores III |
| **Autores** | Edward Ramirez · Dayanna Mendoza · Alejandro Arias |
| **Fecha** | Mayo 2026 |
| **Versión del documento** | 1.0 |

---

## Resumen

iQFleet es un sistema de información propuesto para la ciudad de Valledupar que busca digitalizar y centralizar la gestión administrativa y documental de flotas de transporte público. El sistema reemplaza los procesos manuales actuales por una plataforma unificada que registra conductores, vehículos, documentos legales y movimientos financieros, permitiendo a los propietarios tomar decisiones informadas y oportunas sobre la operación de su negocio.

**Palabras clave:** Eficiencia, Control, Transporte público, Gestión documental, Flota, Valledupar.

---

## 1. Introducción

El transporte público urbano es un servicio esencial en la ciudad de Valledupar, que conecta a miles de ciudadanos con sus destinos cotidianos. Los propietarios de estas flotas gestionan negocios que involucran activos de alto valor —vehículos, licencias, seguros, nóminas— y cuya viabilidad depende de una administración eficiente y oportuna.

Sin embargo, la realidad operativa de muchos de estos propietarios contrasta con esta necesidad: la gestión se lleva a cabo de forma manual, dispersa y sin herramientas que permitan consolidar la información para tomar decisiones estratégicas. iQFleet nace como respuesta a esta brecha entre la complejidad operativa del sector y las herramientas disponibles para administrarlo.

---

## 2. Planteamiento del Problema

En la ciudad de Valledupar existe un número significativo de propietarios de flotas de transporte público que administran sus operaciones mediante registros físicos: cuadernos, carpetas, hojas sueltas o, en el mejor de los casos, hojas de cálculo desconectadas entre sí. Este modelo de gestión presenta las siguientes problemáticas concretas:

### 2.1 Ineficiencia en la búsqueda de información
Localizar el contrato de un conductor, el SOAT de un vehículo o el historial de mantenimiento de una unidad requiere tiempo desproporcionado. La información no está indexada ni centralizada, lo que convierte cada consulta en una búsqueda manual.

### 2.2 Vencimiento descontrolado de documentos legales
Los documentos de tránsito —SOAT, revisión técnico-mecánica, tarjeta de operación, licencias de conducción— tienen fechas de vencimiento que deben monitorearse activamente. Sin un sistema de alertas, un vencimiento no detectado a tiempo deriva en multas, inmovilización del vehículo y sanciones legales que afectan directamente la operación y la economía del negocio.

### 2.3 Ausencia de control financiero claro
Los propietarios no cuentan con reportes consolidados que muestren la rentabilidad real de cada vehículo o de la flota en su conjunto. Sin claridad sobre ingresos, egresos y costos de mantenimiento, las decisiones de inversión, renovación de unidades o contratación se toman sin información sólida.

### 2.4 Riesgo de pérdida de información
Los registros físicos están expuestos a deterioro, extravío, incendio o simplemente al error humano. La pérdida de un documento puede representar la pérdida del historial completo de un vehículo o de un conductor.

### 2.5 Escalabilidad limitada del modelo manual
A medida que la flota crece —más vehículos, más conductores, más trámites— el modelo manual se vuelve inmanejable. No escala sin contratar más personal administrativo, lo que incrementa los costos operativos.

---

## 3. Justificación

La implementación de un sistema digital unificado responde a necesidades reales y verificables del sector. Las razones que justifican el desarrollo de iQFleet son:

- **Impacto directo en la operación:** un control documental eficiente evita multas y sanciones que representan pérdidas económicas concretas para el propietario.
- **Toma de decisiones basada en datos:** los reportes financieros consolidados permiten identificar qué vehículos son rentables, cuáles requieren mayor inversión en mantenimiento y cuándo es conveniente reemplazar una unidad.
- **Reducción del riesgo operativo:** centralizar la información en un sistema con respaldo digital elimina el riesgo de pérdida de documentos críticos.
- **Contexto académico:** el proyecto permite aplicar y demostrar el dominio de conceptos avanzados de programación orientada a objetos, arquitectura de software (Clean Architecture, CQRS), desarrollo de APIs REST con Spring Boot y principios de seguridad informática.

---

## 4. Objetivos

### 4.1 Objetivo General
Desarrollar un sistema de gestión administrativa y documental para flotas de transporte público en Valledupar que centralice el registro de conductores, vehículos, documentos legales y movimientos financieros, mejorando la eficiencia operativa y la capacidad de toma de decisiones de los propietarios.

### 4.2 Objetivos Específicos

1. Implementar un módulo de registro, consulta y seguimiento de conductores con control de estado y vencimiento de licencias.
2. Implementar un módulo de inventario de vehículos con gestión de estado operativo y asignación de conductores.
3. Implementar un módulo de gestión documental con alertas automáticas de vencimiento de documentos legales.
4. Implementar un módulo de control financiero que permita registrar ingresos y egresos y generar reportes de rentabilidad por vehículo y por flota.
5. Implementar un sistema de autenticación y autorización basado en roles que garantice el acceso seguro a la información.
6. Exponer la funcionalidad del sistema mediante una API REST documentada con OpenAPI.

---

## 5. Alcance del Sistema

### 5.1 Incluido en el alcance (MVP)
- Gestión completa (CRUD) de conductores.
- Gestión completa (CRUD) de vehículos con control de estado.
- Registro y control de vencimientos de documentos de conductores y vehículos.
- Registro de movimientos financieros (ingresos y egresos) con reportes de balance.
- Autenticación con JWT y autorización por roles (OWNER, ADMIN).
- API REST documentada con SpringDoc OpenAPI.
- Persistencia en base de datos relacional mediante Spring Data JPA.

### 5.2 Fuera del alcance (versión inicial)
- Interfaz gráfica de usuario (frontend web o móvil).
- Notificaciones por correo electrónico o mensajería móvil.
- Integración con sistemas externos (Runt, Ministerio de Transporte).
- Geolocalización de vehículos en tiempo real.
- Módulo de nómina completo (el registro de pagos a conductores es parte del módulo financiero).

---

## 6. Actores del Sistema

| Actor | Tipo | Descripción |
|-------|------|-------------|
| **Propietario** | Principal | Dueño de la flota. Tiene acceso total al sistema, incluyendo la gestión de usuarios. |
| **Administrador** | Principal | Usuario delegado con permisos de gestión operativa (conductores, vehículos, documentos, finanzas). No puede gestionar usuarios ni configuración del sistema. |
| **Sistema (iQFleet)** | Secundario | Procesa las solicitudes, aplica las reglas de negocio, genera alertas de vencimiento y calcula reportes. |

---

## 7. Metodología de Desarrollo

El proyecto se desarrolla siguiendo una metodología iterativa e incremental, organizada en fases:

| Fase | Actividad | Entregable |
|------|-----------|------------|
| 1 | Definición de requisitos y arquitectura | Este documento + docs de requerimientos |
| 2 | Implementación de la arquitectura base (Clean Architecture + CQRS) | Estructura de paquetes + Mediator |
| 3 | Módulo de conductores (RF-001) | CRUD de conductores funcional |
| 4 | Módulo de vehículos (RF-002) | CRUD de vehículos + asignación |
| 5 | Módulo documental (RF-003) | Gestión de documentos + alertas |
| 6 | Módulo financiero (RF-004) | Movimientos + reportes |
| 7 | Seguridad (RF-005) | JWT + Spring Security |
| 8 | Pruebas e integración | Suite de pruebas + API documentada |

### Patrones y principios aplicados
- **Clean Architecture:** separación estricta entre Domain, Application, Infrastructure y Presentation.
- **CQRS (Command Query Responsibility Segregation):** separación entre operaciones de escritura (Commands) y lectura (Queries).
- **Mediator:** bus de mensajes que desacopla emisores de handlers.
- **Result\<T\>:** manejo explícito de errores sin excepciones como control de flujo.
- **Repository Pattern:** abstracción del acceso a datos en la capa Domain.

---

## 8. Resultados Esperados

Al finalizar el proyecto se espera contar con:

1. Un sistema backend funcional expuesto como API REST, consumible desde cualquier cliente (web, móvil, Postman).
2. Documentación técnica completa: requerimientos, arquitectura, modelo de datos y especificación de la API (Swagger UI).
3. Suite de pruebas unitarias para los handlers de la capa Application.
4. Un sistema que demuestre cómo la digitalización de procesos administrativos puede transformar la gestión de una flota de transporte público en Valledupar.
