package Application.Features.Auth.Commands.ApproveRequest;

import java.util.UUID;

public record AprobarSolicitudResponse(
        UUID solicitudId,
        UUID usuarioId,
        String mensaje
) {}
