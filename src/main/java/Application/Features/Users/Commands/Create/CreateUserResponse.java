package Application.Features.Users.Commands.Create;

import java.util.UUID;

public record CreateUserResponse(UUID userId, String username, String message) {}
