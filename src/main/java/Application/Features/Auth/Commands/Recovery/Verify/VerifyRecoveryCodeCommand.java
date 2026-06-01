package Application.Features.Auth.Commands.Recovery.Verify;

import Application.Abstractions.IRequest;

public record VerifyRecoveryCodeCommand(String email, String code)
        implements IRequest<VerifyRecoveryCodeResponse> {}
