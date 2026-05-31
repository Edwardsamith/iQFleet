package Application.Features.Auth.Queries.GetSolicitudesPendientes;

import Domain.Enums.IdentificationType;
import Domain.Enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitudPendienteResponse(
        UUID solicitudId,
        UUID usuarioId,
        String firstName,
        String lastName,
        String email,
        String username,
        IdentificationType identificationType,
        String identificationNumber,
        String phone,
        Role rolSolicitado,
        LocalDateTime fechaCreacion
) {}
