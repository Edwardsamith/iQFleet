# Patrón CQRS y Mediator en iQFleet

## CQRS — Command Query Responsibility Segregation

CQRS separa las operaciones del sistema en dos tipos:

| Tipo | Qué hace | Retorna |
|------|----------|---------|
| **Command** | Modifica el estado del sistema (crear, actualizar, eliminar) | `Result<Unit>` o `Result<IdCreado>` |
| **Query** | Consulta el estado del sistema sin modificarlo | `Result<T>` con datos |

### Jerarquía de interfaces

```
IRequest<TResponse>
    ├── ICommand<TResponse>    ← para operaciones de escritura
    └── IQuery<TResponse>      ← para operaciones de lectura
```

### Ejemplo: crear un conductor

**Command** (qué quiero hacer):
```java
// Application/Features/Conductores/Commands/Create/CreateConductorCommand.java
public class CreateConductorCommand implements ICommand<Unit> {
    private String cedula;
    private String nombres;
    private String apellidos;
    private String categoriaLicencia;
    private LocalDate vencimientoLicencia;
    // getters/setters con Lombok
}
```

**Handler** (cómo se hace):
```java
// Application/Features/Conductores/Commands/Create/CreateConductorCommandHandler.java
public class CreateConductorCommandHandler
    implements IRequestHandler<CreateConductorCommand, Unit> {

    private final ConductorRepository repository;

    @Override
    public Result<Unit> handle(CreateConductorCommand command) {
        if (repository.existsByCedula(command.getCedula())) {
            return Result.Failure("Ya existe un conductor con esa cédula");
        }
        Conductor conductor = new Conductor();
        conductor.setCedula(command.getCedula());
        // ... mapear campos
        repository.save(conductor);
        return Result.Success();
    }
}
```

---

## Mediator — Bus de mensajes

El `Mediator` desacopla quien envía una solicitud de quien la procesa.

### Flujo

```
Controller
    │  mediator.send(new CreateConductorCommand(...))
    ▼
Mediator
    │  busca handler registrado para CreateConductorCommand
    ▼
CreateConductorCommandHandler.handle(command)
    │
    ▼
Result<Unit>  →  Controller  →  ResponseEntity
```

### Registro de handlers

En el contexto de Spring Boot, los handlers se registran automáticamente vía inyección de dependencias. Sin Spring (modo consola), se registran manualmente:

```java
Mediator mediator = new Mediator();
mediator.registerHandler(CreateConductorCommand.class,
    new CreateConductorCommandHandler(conductorRepo));
```

Con Spring Boot, el `Mediator` se configura como `@Bean` y los handlers se inyectan como `@Component`.

---

## Result<T> — Manejo de errores sin excepciones

`Result<T>` es el tipo de retorno de todos los handlers. Evita usar excepciones como control de flujo.

```java
// Éxito sin valor
Result.Success()                    // → Result<Unit>

// Éxito con valor
Result.Success(conductor)           // → Result<Conductor>

// Fallo con un error
Result.Failure("Cédula duplicada")  // → Result<T>

// Fallo con múltiples errores
Result.Failure(listaDErrores)       // → Result<T>
```

**En el controller:**
```java
Result<Unit> result = mediator.send(command);
if (!result.isSuccess()) {
    return ResponseEntity.badRequest().body(result.getErrors());
}
return ResponseEntity.created(...).build();
```

---

## Ventajas de este enfoque en iQFleet

1. **Escalabilidad de features:** agregar un nuevo caso de uso es crear un Command + Handler, sin modificar código existente (principio Open/Closed).
2. **Testabilidad:** cada Handler es una clase con una responsabilidad → fácil de probar unitariamente.
3. **Trazabilidad:** el Mediator puede extenderse para agregar logging, validación o transacciones transversales sin tocar los handlers.
4. **Claridad:** el nombre del Command describe exactamente qué operación se ejecuta (`CreateConductorCommand`, `RenovarDocumentoCommand`).
