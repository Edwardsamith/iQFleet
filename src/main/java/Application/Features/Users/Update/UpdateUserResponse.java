package Application.Features.Users.Update;

import java.util.UUID;

public record UpdateUserResponse(
        UUID id,
        String username,
        String message
) {
}