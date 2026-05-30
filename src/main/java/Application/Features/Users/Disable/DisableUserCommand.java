package Application.Features.Users.Disable;

import Application.Abstractions.ICommand;

import java.util.UUID;

public record DisableUserCommand(
        UUID userId
) implements ICommand<String> {
}