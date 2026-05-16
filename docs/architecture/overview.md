# Visión General de la Arquitectura — iQFleet

## Diagrama de capas

```
┌─────────────────────────────────────────────────────┐
│                   Presentation                       │
│         (Spring Boot Controllers, DTOs)              │
│              src/main/java/Presentation              │
└───────────────────────┬─────────────────────────────┘
                        │ usa
┌───────────────────────▼─────────────────────────────┐
│                   Application                        │
│    (Features / Commands / Queries / Mediator)        │
│              src/main/java/Application               │
└───────────┬──────────────────────────┬──────────────┘
            │ depende de               │ define contratos
┌───────────▼──────────┐  ┌────────────▼──────────────┐
│       Domain          │  │      Infrastructure        │
│  (Entities, Repos,    │  │  (JPA/File Repositories,   │
│   Exceptions,         │  │   Hibernate, Persistence)  │
│   Value Objects)      │  │  src/.../Infrastructure    │
│  src/.../Domain       │  └────────────────────────────┘
└───────────────────────┘
```

**Regla fundamental:** las flechas solo apuntan hacia adentro. `Domain` no conoce ninguna capa exterior.

---

## Paquetes actuales

| Paquete | Responsabilidad |
|---------|----------------|
| `Domain.Entities` | Modelos de negocio (sin dependencias de frameworks) |
| `Domain.Repositories` | Interfaces de acceso a datos (contratos) |
| `Domain.Exceptions` | Excepciones de dominio |
| `Application.Abstractions` | Interfaces del patrón Mediator (IRequest, ICommand, IQuery, IRequestHandler, IMediator) |
| `Application.Features` | Casos de uso organizados por Feature → Command/Query |
| `Application.Result` | Tipo `Result<T>` para respuestas sin excepciones |
| `Application.Mediator` | Implementación del bus de mensajes |
| `Infrastructure.Repositories` | Implementaciones concretas (File, Hibernate) |
| `Infrastructure.Persistence` | Configuración de Hibernate/JPA |
| `Presentation` | Spring Boot Application (punto de entrada REST) |
| `Console` | Main de prueba (temporal) |

---

## Flujo de una petición

```
HTTP Request
    │
    ▼
[Controller] — crea Command o Query
    │
    ▼
[Mediator.send(request)]
    │
    ▼
[Handler.handle(request)] — lógica de aplicación
    │
    ├── [Repository] — acceso a datos (interfaz de Domain)
    │       │
    │       └── [HibernateRepository / FileRepository] — implementación de Infrastructure
    │
    ▼
[Result<T>] — respuesta tipada (éxito o errores)
    │
    ▼
[Controller] — mapea Result a ResponseEntity HTTP
    │
    ▼
HTTP Response
```

---

## Decisiones de diseño

| Decisión | Razón |
|----------|-------|
| Clean Architecture | Permite cambiar la infraestructura (File → Hibernate → cualquier DB) sin tocar la lógica de negocio |
| CQRS con Mediator | Separa lecturas de escrituras; facilita agregar handlers sin modificar código existente |
| `Result<T>` en lugar de excepciones | Hace explícito si una operación puede fallar; evita excepciones como control de flujo |
| Lombok | Reduce código repetitivo en entidades y DTOs |
| SpringDoc OpenAPI | Documentación de API generada automáticamente desde el código |
