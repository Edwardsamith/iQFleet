# Clean Architecture en iQFleet

## Principio

La arquitectura del proyecto aplica los principios de **Clean Architecture** (Robert C. Martin): las capas internas no deben conocer las capas externas. La lógica de negocio es independiente de frameworks, bases de datos e interfaces de usuario.

## Regla de dependencia

```
Presentation → Application → Domain ← Infrastructure
```

- `Domain` no importa nada de los otros paquetes.
- `Application` solo importa de `Domain`.
- `Infrastructure` implementa las interfaces de `Domain`.
- `Presentation` orquesta usando `Application`.

## Capa Domain

Contiene el corazón del negocio. Sin ninguna dependencia externa (ni Spring, ni Hibernate).

**Qué va aquí:**
- Entidades (`Conductor`, `Vehiculo`, `Documento`, `MovimientoFinanciero`, `Usuario`)
- Interfaces de repositorios (`ConductorRepository`, `VehiculoRepository`, etc.)
- Excepciones de dominio (`ConductorNotFoundException`, `PlacaDuplicadaException`, etc.)
- Value Objects (si aplica, ej. `Placa`, `Cedula`)

**Ejemplo actual:**
```
Domain/
├── Entities/
│   ├── Entity.java          ← clase base con id
│   └── Example.java         ← (será reemplazado por entidades reales)
├── Repositories/
│   ├── Repository.java      ← interfaz genérica CRUD
│   └── ExampleRepository.java
└── Exceptions/
    └── ApplicationsExepctions.java
```

## Capa Application

Orquesta los casos de uso. Conoce el dominio pero no sabe cómo se persisten los datos ni cómo se expone la API.

**Qué va aquí:**
- Abstracciones del patrón Mediator (`IRequest`, `ICommand`, `IQuery`, `IRequestHandler`, `IMediator`)
- Features organizados por módulo: `Features/Conductores/Commands/Create/`, `Features/Conductores/Queries/GetById/`, etc.
- Implementación del Mediator
- Tipo `Result<T>` y `Unit`

**Estructura objetivo por módulo:**
```
Application/
└── Features/
    └── Conductores/
        ├── Commands/
        │   ├── Create/
        │   │   ├── CreateConductorCommand.java
        │   │   └── CreateConductorCommandHandler.java
        │   └── Update/
        │       ├── UpdateConductorCommand.java
        │       └── UpdateConductorCommandHandler.java
        └── Queries/
            └── GetById/
                ├── GetConductorByIdQuery.java
                └── GetConductorByIdQueryHandler.java
```

## Capa Infrastructure

Implementa los contratos definidos en `Domain`. Aquí viven los detalles técnicos.

**Qué va aquí:**
- Implementaciones de repositorios con Hibernate/JPA (`HibernateConductorRepository`)
- Configuración de persistencia (`HibernateUtil`)
- Implementaciones alternativas (File, in-memory) para pruebas

**Ventaja:** si se cambia de Hibernate a otro ORM, solo cambia `Infrastructure`. La lógica de negocio en `Application` y `Domain` no se toca.

## Capa Presentation

Expone la aplicación al mundo exterior vía HTTP REST.

**Qué va aquí:**
- Controllers de Spring Boot (`@RestController`)
- DTOs de request y response
- Configuración de Spring Security
- Mapeo entre DTOs y Commands/Queries

**Qué NO va aquí:**
- Lógica de negocio
- Acceso directo a repositorios
- Validaciones de dominio
