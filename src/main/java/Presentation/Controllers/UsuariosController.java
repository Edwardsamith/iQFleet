package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Auth.Commands.ApproveRequest.AprobarSolicitudCommand;
import Application.Features.Auth.Commands.ApproveRequest.AprobarSolicitudResponse;
import Application.Features.Auth.Commands.RejectRequest.RechazarSolicitudCommand;
import Application.Features.Auth.Commands.RejectRequest.RechazarSolicitudResponse;
import Application.Features.Auth.Queries.GetSolicitudesPendientes.GetSolicitudesPendientesQuery;
import Application.Features.Auth.Queries.GetSolicitudesPendientes.SolicitudPendienteResponse;
import Application.Result.Result;
import Domain.Enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

    private final IMediator mediator;

    @GetMapping("/solicitudes")
    public ResponseEntity<?> listarSolicitudesPendientes() {
        Result<List<SolicitudPendienteResponse>> result = mediator.send(new GetSolicitudesPendientesQuery());

        if (!result.isSuccess()) {
            return ResponseEntity.internalServerError().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    @PatchMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable UUID id,
            @RequestBody(required = false) AprobarRequest body,
            Authentication authentication
    ) {
        UUID adminId = resolveAdminId(authentication);

        Role rolAsignado = null;
        if (body != null && body.rol() != null && !body.rol().isBlank()) {
            try {
                rolAsignado = Role.valueOf(body.rol());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(List.of("Rol inválido: " + body.rol()));
            }
        }

        Result<AprobarSolicitudResponse> result = mediator.send(
                new AprobarSolicitudCommand(id, adminId, rolAsignado)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    @PatchMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazar(
            @PathVariable UUID id,
            @RequestBody(required = false) RechazarRequest body,
            Authentication authentication
    ) {
        UUID adminId = resolveAdminId(authentication);
        String motivo = body != null ? body.motivoRechazo() : null;

        Result<RechazarSolicitudResponse> result = mediator.send(
                new RechazarSolicitudCommand(id, adminId, motivo)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

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

    record AprobarRequest(String rol) {}
    record RechazarRequest(String motivoRechazo) {}
}
