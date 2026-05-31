package Application.Features.Users.Commands.Activate;

import Application.Abstractions.ICommand;

import java.util.UUID;

public record ActivateUserCommand(
        UUID userId
) implements ICommand<String> {
}
