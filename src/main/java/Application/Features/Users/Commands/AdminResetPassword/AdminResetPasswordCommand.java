package Application.Features.Users.Commands.AdminResetPassword;

import Application.Abstractions.IRequest;

import java.util.UUID;

public record AdminResetPasswordCommand(UUID userId, String newPassword)
        implements IRequest<String> {}
