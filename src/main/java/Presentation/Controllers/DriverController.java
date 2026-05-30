package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Drivers.Commands.ChangeStatus.ChangeDriverStatusCommand;
import Application.Features.Drivers.Commands.Create.CreateDriverCommand;
import Application.Features.Drivers.Commands.Update.UpdateDriverCommand;
import Application.Features.Drivers.Queries.GetByFilter.GetDriversByFilterQuery;
import Application.Features.Drivers.Queries.GetById.GetDriverByIdQuery;
import Application.Result.Result;
import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;
import Domain.Enums.IdentificationType;
import Domain.Enums.LicenseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final IMediator mediator;

    // POST /api/drivers
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDriverRequest request) {

        Result<Application.Result.Unit> result = mediator.send(
                new CreateDriverCommand(
                        request.identificationType(),
                        request.identificationNumber(),
                        request.firstName(),
                        request.lastName(),
                        request.licenseNumber(),
                        request.licenseCategory(),
                        request.licenseExpiry(),
                        request.phone(),
                        request.email(),
                        request.address(),
                        request.registrationDate()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET /api/drivers
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(required = false) LicenseCategory licenseCategory,
            @RequestParam(required = false) Integer expiresInDays) {

        Result<List<Driver>> result = mediator.send(
                new GetDriversByFilterQuery(status, licenseCategory, expiresInDays)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // GET /api/drivers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        Result<Driver> result = mediator.send(new GetDriverByIdQuery(id));

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getValue());
    }

    // PUT /api/drivers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody UpdateDriverRequest request) {

        Result<Application.Result.Unit> result = mediator.send(
                new UpdateDriverCommand(
                        id,
                        request.firstName(),
                        request.lastName(),
                        request.licenseCategory(),
                        request.licenseExpiry(),
                        request.phone(),
                        request.email(),
                        request.address()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // PATCH /api/drivers/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable UUID id,
            @RequestBody ChangeStatusRequest request) {

        Result<Application.Result.Unit> result = mediator.send(
                new ChangeDriverStatusCommand(id, request.status())
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // ─── DTOs de request (Records de Java) ───────────────────────────

    record CreateDriverRequest(
            IdentificationType identificationType,
            String identificationNumber,
            String firstName,
            String lastName,
            String licenseNumber,
            LicenseCategory licenseCategory,
            LocalDate licenseExpiry,
            String phone,
            String email,
            String address,
            LocalDate registrationDate
    ) {}

    record UpdateDriverRequest(
            String firstName,
            String lastName,
            LicenseCategory licenseCategory,
            LocalDate licenseExpiry,
            String phone,
            String email,
            String address
    ) {}

    record ChangeStatusRequest(DriverStatus status) {}
}
