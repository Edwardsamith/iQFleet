package Application.Features.Auth.Commands.Recovery.Request;

import Application.Abstractions.IRequest;

public record RecoverPasswordCommand(String method, String recipient)
        implements IRequest<RecoverPasswordResponse> {}
