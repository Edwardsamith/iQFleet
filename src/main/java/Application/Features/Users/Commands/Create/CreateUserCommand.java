package Application.Features.Users.Commands.Create;

import Application.Abstractions.IRequest;
import Domain.Enums.IdentificationType;
import Domain.Enums.Role;
import Domain.Enums.UserStatus;

public record CreateUserCommand(
        String firstName,
        String lastName,
        IdentificationType identificationType,
        String identificationNumber,
        String email,
        String phone,
        String username,
        String password,
        Role role,
        UserStatus status
) implements IRequest<CreateUserResponse> {}
