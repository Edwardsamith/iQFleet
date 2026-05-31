package Application.Features.Users.Commands.Update;

import Application.Abstractions.ICommand;
import Domain.Enums.Role;

import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        String firstName,
        String lastName,
        String phone,
        String email,
        Role role
) implements ICommand<UpdateUserResponse> {
}
