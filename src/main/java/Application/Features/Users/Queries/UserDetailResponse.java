package Application.Features.Users.Queries;

import java.util.UUID;

public record UserDetailResponse(
        UUID id,
        String firstName,
        String lastName,
        String identificationType,
        String identificationNumber,
        String email,
        String phone,
        String username,
        String role,
        String status,
        String lastAccess,
        String createdAt
) {}
