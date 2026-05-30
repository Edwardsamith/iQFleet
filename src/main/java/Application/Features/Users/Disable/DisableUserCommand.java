package Application.Features.Users.Disable;

import Application.Abstractions.ICommand;

public record DisableUserCommand(
        Long userId
) implements ICommand<String> {
}