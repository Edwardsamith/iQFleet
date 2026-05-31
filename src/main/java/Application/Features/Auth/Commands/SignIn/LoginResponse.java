package Application.Features.Auth.Commands.SignIn;

public record LoginResponse(
        String token,
        String role,
        String username,
        String email,
        String firstName,
        String lastName
) {}
