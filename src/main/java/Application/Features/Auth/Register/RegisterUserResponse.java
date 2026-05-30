package Application.Features.Auth.Register;

import java.util.UUID;

public record RegisterUserResponse(
    UUID userId,
    String username,
    String message
    ){

}
