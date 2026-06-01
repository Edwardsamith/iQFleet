package Application.Features.Auth.Commands.Recovery.Reset;

import Application.Abstractions.IRequest;

public record ResetPasswordCommand(String resetToken, String newPassword)
        implements IRequest<ResetPasswordResponse> {}
