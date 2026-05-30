package Application.User.Register;

import Application.Abstractions.ICommand;
import Domain.Enums.Role;
import Domain.Enums.IdentificationType;

public record RegisterUserCommand(
        String firstName,
        String lastName,
        IdentificationType identificationType,
        String identificationNumber,
        String email,
        String phone,
        String username,
        String password,
        Role role
) implements ICommand<RegisterUserResponse> {

}
