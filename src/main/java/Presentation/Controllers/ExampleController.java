package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Example.Commands.Create.CreateExampleCommand;
import Application.Features.Example.Commands.Delete.DeleteExampleCommand;
import Application.Features.Example.Commands.Update.UpdateExampleCommand;
import Application.Features.Example.Queries.GetAll.GetAllExamplesQuery;
import Application.Features.Example.Queries.GetById.GetExampleByIdQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/examples")
@Tag(name = "Examples", description = "CRUD de ejemplos")
public class ExampleController {

    private final IMediator mediator;

    public ExampleController(IMediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Crear un ejemplo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateExampleRequest request) {
        var result = mediator.send(new CreateExampleCommand(request.name(), request.apellido()));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Obtener todos los ejemplos")
    @ApiResponse(responseCode = "200", description = "Lista de ejemplos")
    @GetMapping
    public ResponseEntity<?> getAll() {
        var result = mediator.send(new GetAllExamplesQuery());
        if (!result.isSuccess()) {
            return ResponseEntity.internalServerError().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @Operation(summary = "Obtener un ejemplo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejemplo encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "UUID del ejemplo") @PathVariable UUID id) {
        var result = mediator.send(new GetExampleByIdQuery(id));
        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.getValue());
    }

    @Operation(summary = "Actualizar un ejemplo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "UUID del ejemplo") @PathVariable UUID id,
            @RequestBody UpdateExampleRequest request) {
        var result = mediator.send(new UpdateExampleCommand(id, request.name(), request.apellido()));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar un ejemplo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se pudo eliminar")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "UUID del ejemplo") @PathVariable UUID id) {
        var result = mediator.send(new DeleteExampleCommand(id));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.noContent().build();
    }

    public record CreateExampleRequest(String name, String apellido) {}
    public record UpdateExampleRequest(String name, String apellido) {}
}
