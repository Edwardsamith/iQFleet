package Application.Features.Auth.Queries.GetPerfil;

import Domain.Enums.Role;
import Domain.Enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetPerfilUsuarioResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String username,
        String phone,
        Role role,
        UserStatus status,
        LocalDateTime lastAccess
) {}
