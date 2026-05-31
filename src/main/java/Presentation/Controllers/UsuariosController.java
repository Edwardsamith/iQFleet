package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Auth.Commands.ApproveRequest.AprobarSolicitudCommand;
import Application.Features.Auth.Commands.ApproveRequest.AprobarSolicitudResponse;
import Application.Features.Auth.Commands.RejectRequest.RechazarSolicitudCommand;
import Application.Features.Auth.Commands.RejectRequest.RechazarSolicitudResponse;
import Application.Features.Auth.Queries.GetSolicitudesPendientes.GetSolicitudesPendientesQuery;
import Application.Features.Auth.Queries.GetSolicitudesPendientes.SolicitudPendienteResponse;
import Application.Features.Users.Commands.AdminResetPassword.AdminResetPasswordCommand;
import Application.Features.Users.Commands.ChangeStatus.ChangeUserStatusCommand;
import Application.Features.Users.Commands.Create.CreateUserCommand;
import Application.Features.Users.Commands.Create.CreateUserResponse;
import Application.Features.Users.Commands.Update.UpdateUserCommand;
import Application.Features.Users.Commands.Update.UpdateUserResponse;
import Application.Features.Users.Queries.GetByFilter.GetUsersByFilterQuery;
import Application.Features.Users.Queries.GetById.GetUserByIdQuery;
import Application.Features.Users.Queries.UserDetailResponse;
import Application.Result.Result;
import Domain.Enums.IdentificationType;
import Domain.Enums.Role;
import Domain.Enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuariosController {

    private final IMediator mediator;

    // ─── RF-008: User management ──────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        IdentificationType idType;
        try {
            idType = IdentificationType.valueOf(request.identificationType());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(List.of("Tipo de identificación inválido"));
        }

        Role role;
        try {
            role = Role.valueOf(request.role());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(List.of("Rol inválido"));
        }

        UserStatus status = UserStatus.ACTIVE;
        if (request.status() != null && !request.status().isBlank()) {
            try {
                status = UserStatus.valueOf(request.status());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(List.of("Estado inválido"));
            }
        }

        Result<CreateUserResponse> result = mediator.send(new CreateUserCommand(
                request.firstName(), request.lastName(), idType,
                request.identificationNumber(), request.email(), request.phone(),
                request.username(), request.password(), role, status
        ));

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.status(201).body(result.getValue());
    }

    @GetMapping
    public ResponseEntity<?> listUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role
    ) {
        Result<List<UserDetailResponse>> result = mediator.send(new GetUsersByFilterQuery(status, role));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        Result<UserDetailResponse> result = mediator.send(new GetUserByIdQuery(id));
        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request
    ) {
        Role role = null;
        if (request.role() != null && !request.role().isBlank()) {
            try {
                role = Role.valueOf(request.role());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(List.of("Rol inválido"));
            }
        }

        Result<UpdateUserResponse> result = mediator.send(new UpdateUserCommand(
                id, request.firstName(), request.lastName(), request.phone(), request.email(), role
        ));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable UUID id,
            @RequestBody ChangeStatusRequest request
    ) {
        UserStatus newStatus;
        try {
            newStatus = UserStatus.valueOf(request.status());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(List.of("Estado inválido"));
        }

        Result<String> result = mediator.send(new ChangeUserStatusCommand(id, newStatus));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(
            @PathVariable UUID id,
            @RequestBody AdminResetPasswordRequest request
    ) {
        Result<String> result = mediator.send(new AdminResetPasswordCommand(id, request.newPassword()));
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    // ─── RF-006: Registration requests ───────────────────────────────────────

    @GetMapping("/requests")
    public ResponseEntity<?> listPendingRequests() {
        Result<List<SolicitudPendienteResponse>> result = mediator.send(new GetSolicitudesPendientesQuery());
        if (!result.isSuccess()) {
            return ResponseEntity.internalServerError().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PatchMapping("/requests/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable UUID id,
            @RequestBody(required = false) ApproveRequest body,
            Authentication authentication
    ) {
        UUID adminId = resolveAdminId(authentication);

        Role assignedRole = null;
        if (body != null && body.role() != null && !body.role().isBlank()) {
            try {
                assignedRole = Role.valueOf(body.role());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(List.of("Rol inválido: " + body.role()));
            }
        }

        Result<AprobarSolicitudResponse> result = mediator.send(
                new AprobarSolicitudCommand(id, adminId, assignedRole)
        );
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PatchMapping("/requests/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable UUID id,
            @RequestBody(required = false) RejectRequest body,
            Authentication authentication
    ) {
        UUID adminId = resolveAdminId(authentication);
        String reason = body != null ? body.reason() : null;

        Result<RechazarSolicitudResponse> result = mediator.send(
                new RechazarSolicitudCommand(id, adminId, reason)
        );
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private UUID resolveAdminId(Authentication authentication) {
        if (authentication == null) return null;
        Object credentials = authentication.getCredentials();
        if (credentials instanceof UUID uuid) return uuid;
        try {
            return UUID.fromString(String.valueOf(credentials));
        } catch (Exception e) {
            return null;
        }
    }

    // ─── Request records ──────────────────────────────────────────────────────

    record CreateUserRequest(
            String firstName, String lastName, String identificationType,
            String identificationNumber, String email, String phone,
            String username, String password, String role, String status
    ) {}

    record UpdateUserRequest(
            String firstName, String lastName, String phone, String email, String role
    ) {}

    record ChangeStatusRequest(String status) {}

    record AdminResetPasswordRequest(String newPassword) {}

    record ApproveRequest(String role) {}

    record RejectRequest(String reason) {}
}
