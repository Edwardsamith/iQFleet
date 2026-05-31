package Application.Features.Users.Commands.ChangeStatus;

import Application.Abstractions.IRequest;
import Domain.Enums.UserStatus;

import java.util.UUID;

public record ChangeUserStatusCommand(UUID userId, UserStatus newStatus)
        implements IRequest<String> {}
