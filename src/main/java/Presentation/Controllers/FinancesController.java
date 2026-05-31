package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Finances.Commands.Cancel.CancelMovementCommand;
import Application.Features.Finances.Commands.Register.RegisterMovementCommand;
import Application.Features.Finances.Common.FleetBalanceResponse;
import Application.Features.Finances.Common.VehicleBalanceResponse;
import Application.Features.Finances.Queries.GetBalance.GetFleetBalanceQuery;
import Application.Features.Finances.Queries.GetBalance.GetVehicleBalanceQuery;
import Application.Features.Finances.Queries.GetByFilter.GetMovementsByFilterQuery;
import Application.Features.Finances.Queries.GetById.GetMovementByIdQuery;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finances")
@RequiredArgsConstructor
public class FinancesController {

    private final IMediator mediator;

    // POST /api/finances/movements
    @PostMapping("/movements")
    public ResponseEntity<?> register(@RequestBody RegisterMovementRequest request) {

        Result<Unit> result = mediator.send(
                new RegisterMovementCommand(
                        request.movementType(),
                        request.category(),
                        request.paymentMethod(),
                        request.amount(),
                        request.date(),
                        request.description(),
                        request.notes(),
                        request.vehicleId(),
                        request.driverId(),
                        request.registeredBy()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET /api/finances/movements
    @GetMapping("/movements")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) MovementType movementType,
            @RequestParam(required = false) MovementCategory category,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        Result<List<FinancialMovement>> result = mediator.send(
                new GetMovementsByFilterQuery(movementType, category, paymentMethod, vehicleId, dateFrom, dateTo)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // GET /api/finances/movements/{id}
    @GetMapping("/movements/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        Result<FinancialMovement> result = mediator.send(new GetMovementByIdQuery(id));

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getValue());
    }

    // PATCH /api/finances/movements/{id}/cancel
    @PatchMapping("/movements/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID id) {

        Result<Unit> result = mediator.send(new CancelMovementCommand(id));

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // GET /api/finances/balance/vehicle/{vehicleId}
    @GetMapping("/balance/vehicle/{vehicleId}")
    public ResponseEntity<?> getVehicleBalance(
            @PathVariable UUID vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        Result<VehicleBalanceResponse> result = mediator.send(
                new GetVehicleBalanceQuery(vehicleId, dateFrom, dateTo)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // GET /api/finances/balance/fleet
    @GetMapping("/balance/fleet")
    public ResponseEntity<?> getFleetBalance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        Result<FleetBalanceResponse> result = mediator.send(
                new GetFleetBalanceQuery(dateFrom, dateTo)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // ─── Request DTOs ─────────────────────────────────────────────────

    record RegisterMovementRequest(
            MovementType movementType,
            MovementCategory category,
            PaymentMethod paymentMethod,
            BigDecimal amount,
            LocalDate date,
            String description,
            String notes,
            UUID vehicleId,
            UUID driverId,
            String registeredBy
    ) {}
}
