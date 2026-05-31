package Application.Features.Users.Commands.Update;

import java.util.UUID;

public record UpdateUserResponse(
        UUID id,
        String username,
        String message
) {
}