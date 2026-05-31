package Application.Features.Auth.Commands.SignIn;

import Application.Abstractions.ICommand;

public record LoginCommand(
        String email,
        String password
) implements ICommand<LoginResponse> {}
