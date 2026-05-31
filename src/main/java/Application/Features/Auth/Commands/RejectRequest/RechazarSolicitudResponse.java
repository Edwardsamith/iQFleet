package Application.Features.Auth.Commands.RejectRequest;

import java.util.UUID;

public record RechazarSolicitudResponse(
        UUID solicitudId,
        UUID usuarioId,
        String mensaje
) {}
