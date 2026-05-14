# iQFleet — Documentación del Proyecto

> **Sistema de gestión administrativa y documental de una flota de transporte público**
> Ciudad de Valledupar · Mayo 2026

| Campo | Detalle |
|-------|---------|
| **Institución** | Universidad Popular del Cesar |
| **Facultad** | Ingeniería |
| **Asignatura** | Programación de Computadores III |
| **Autores** | Edward Ramirez · Dayanna Mendoza · Alejandro Arias |
| **Versión** | 1.0 |
| **Fecha** | Mayo 2026 |

---

## Tabla de Contenidos

### 1. Proyecto
| Documento | Descripción |
|-----------|-------------|
| [Descripción general](proyecto/descripcion-general.md) | Resumen, problemática, objetivos, alcance y metodología |
| [Glosario](proyecto/glosario.md) | Definición de términos clave del dominio y del sistema |

### 2. Requerimientos Funcionales

| ID | Módulo | Prioridad | Documento |
|----|--------|-----------|-----------|
| RF-001 | Gestión de Conductores | Alta | [RF-001-gestion-conductores.md](requirements/RF-001-gestion-conductores.md) |
| RF-002 | Gestión de Vehículos | Alta | [RF-002-gestion-vehiculos.md](requirements/RF-002-gestion-vehiculos.md) |
| RF-003 | Gestión Documental y Vencimientos | Alta | [RF-003-gestion-documentos.md](requirements/RF-003-gestion-documentos.md) |
| RF-004 | Gestión Financiera | Alta | [RF-004-control-financiero.md](requirements/RF-004-control-financiero.md) |
| RF-005 | Autenticación y Acceso | Alta | [RF-005-autenticacion-usuarios.md](requirements/RF-005-autenticacion-usuarios.md) |
| RF-006 | Solicitud y Registro de Usuarios | Alta | [RF-006-registro-usuarios.md](requirements/RF-006-registro-usuarios.md) |
| RF-007 | Recuperación de Contraseña | Media | [RF-007-recuperacion-contrasena.md](requirements/RF-007-recuperacion-contrasena.md) |
| RF-008 | Gestión de Usuarios | Alta | [RF-008-gestion-usuarios.md](requirements/RF-008-gestion-usuarios.md) |
| RF-009 | Panel Administrativo y Métricas | Alta | [RF-009-panel-metricas.md](requirements/RF-009-panel-metricas.md) |
| RF-010 | Generación de Reportes | Media-Alta | [RF-010-generacion-reportes.md](requirements/RF-010-generacion-reportes.md) |

### 3. Requerimientos No Funcionales

| ID | Categoría | Documento |
|----|-----------|-----------|
| RNF-001 | Seguridad | [RNF-001-requisitos-no-funcionales.md](requirements/RNF-001-requisitos-no-funcionales.md) |
| RNF-002 | Rendimiento | [RNF-001-requisitos-no-funcionales.md](requirements/RNF-001-requisitos-no-funcionales.md) |
| RNF-003 | Usabilidad de la API | [RNF-001-requisitos-no-funcionales.md](requirements/RNF-001-requisitos-no-funcionales.md) |
| RNF-004 | Disponibilidad y Confiabilidad | [RNF-001-requisitos-no-funcionales.md](requirements/RNF-001-requisitos-no-funcionales.md) |
| RNF-005 | Mantenibilidad | [RNF-001-requisitos-no-funcionales.md](requirements/RNF-001-requisitos-no-funcionales.md) |
| RNF-006 | Portabilidad y Configuración | [RNF-001-requisitos-no-funcionales.md](requirements/RNF-001-requisitos-no-funcionales.md) |

### 4. Arquitectura

| Documento | Descripción |
|-----------|-------------|
| [Visión general](architecture/overview.md) | Diagrama de capas, flujo de petición y decisiones de diseño |
| [Clean Architecture](architecture/clean-architecture.md) | Qué pertenece a cada capa y la regla de dependencia |
| [CQRS y Mediator](architecture/cqrs-mediator.md) | Patrón de mensajería, Result\<T\> y ejemplos de código |
| [Modelo de datos](architecture/modelo-datos.md) | Entidades, atributos y relaciones del sistema |

### 5. Casos de Uso

| Documento | Descripción |
|-----------|-------------|
| [Diagrama de casos de uso](casos-de-uso/casos-de-uso.md) | Actores, casos de uso y relaciones del sistema |

---

## Stack tecnológico

| Tecnología | Versión | Rol |
|------------|---------|-----|
| Java | 25 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework web e inyección de dependencias |
| Spring Data JPA | — | Acceso y persistencia de datos |
| Spring Security | — | Autenticación JWT y autorización por roles |
| Hibernate | — | ORM para mapeo objeto-relacional |
| SpringDoc OpenAPI | 3.0.2 | Documentación automática de la API REST |
| Lombok | — | Reducción de código repetitivo |
| Maven | — | Gestión de dependencias y ciclo de construcción |

---

## Estructura del proyecto

```
iQFleet/
├── docs/
│   ├── README.md                                    (este archivo)
│   ├── proyecto/
│   │   ├── descripcion-general.md
│   │   └── glosario.md
│   ├── requirements/
│   │   ├── RF-001-gestion-conductores.md
│   │   ├── RF-002-gestion-vehiculos.md
│   │   ├── RF-003-gestion-documentos.md
│   │   ├── RF-004-control-financiero.md
│   │   ├── RF-005-autenticacion-usuarios.md
│   │   ├── RF-006-registro-usuarios.md
│   │   ├── RF-007-recuperacion-contrasena.md
│   │   ├── RF-008-gestion-usuarios.md
│   │   ├── RF-009-panel-metricas.md
│   │   ├── RF-010-generacion-reportes.md
│   │   └── RNF-001-requisitos-no-funcionales.md
│   ├── architecture/
│   │   ├── overview.md
│   │   ├── clean-architecture.md
│   │   ├── cqrs-mediator.md
│   │   └── modelo-datos.md
│   └── casos-de-uso/
│       └── casos-de-uso.md
└── src/
    └── main/java/
        ├── Domain/
        │   ├── Entities/
        │   ├── Repositories/
        │   └── Exceptions/
        ├── Application/
        │   ├── Abstractions/
        │   ├── Features/
        │   ├── Result/
        │   └── Mediator.java
        ├── Infrastructure/
        │   ├── Repositories/
        │   └── Persistence/
        └── Presentation/
            └── IQFleetApplication.java
```
