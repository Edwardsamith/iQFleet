package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Vehicles.Commands.AssignDriver.AssignDriverCommand;
import Application.Features.Vehicles.Commands.ChangeStatus.ChangeVehicleStatusCommand;
import Application.Features.Vehicles.Commands.Create.CreateVehicleCommand;
import Application.Features.Vehicles.Commands.Update.UpdateVehicleCommand;
import Application.Features.Vehicles.Queries.GetByFilter.GetVehiclesByFilterQuery;
import Application.Features.Vehicles.Queries.GetById.GetVehicleByIdQuery;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final IMediator mediator;

    // POST /api/vehicles
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateVehicleRequest request) {

        Result<Unit> result = mediator.send(
                new CreateVehicleCommand(
                        request.plateNumber(),
                        request.brand(),
                        request.vehicleModel(),
                        request.vehicleType(),
                        request.year(),
                        request.notes(),
                        request.registrationDate(),
                        request.responsibleId()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET /api/vehicles
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) Boolean withoutDriver) {

        Result<List<Vehicle>> result = mediator.send(
                new GetVehiclesByFilterQuery(status, vehicleType, withoutDriver)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // GET /api/vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        Result<Vehicle> result = mediator.send(new GetVehicleByIdQuery(id));

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getValue());
    }

    // PUT /api/vehicles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody UpdateVehicleRequest request) {

        Result<Unit> result = mediator.send(
                new UpdateVehicleCommand(
                        id,
                        request.brand(),
                        request.vehicleModel(),
                        request.vehicleType(),
                        request.year(),
                        request.notes(),
                        request.responsibleId()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // PATCH /api/vehicles/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable UUID id,
            @RequestBody ChangeVehicleStatusRequest request) {

        Result<Unit> result = mediator.send(
                new ChangeVehicleStatusCommand(id, request.status())
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // PATCH /api/vehicles/{id}/driver
    @PatchMapping("/{id}/driver")
    public ResponseEntity<?> assignDriver(
            @PathVariable UUID id,
            @RequestBody AssignDriverRequest request) {

        Result<Unit> result = mediator.send(
                new AssignDriverCommand(id, request.driverId())
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // ─── DTOs ────────────────────────────────────────────────────────

    record CreateVehicleRequest(
            String plateNumber,
            String brand,
            String vehicleModel,
            VehicleType vehicleType,
            Integer year,
            String notes,
            LocalDate registrationDate,
            UUID responsibleId
    ) {}

    record UpdateVehicleRequest(
            String brand,
            String vehicleModel,
            VehicleType vehicleType,
            Integer year,
            String notes,
            UUID responsibleId
    ) {}

    record ChangeVehicleStatusRequest(VehicleStatus status) {}

    record AssignDriverRequest(UUID driverId) {}
}