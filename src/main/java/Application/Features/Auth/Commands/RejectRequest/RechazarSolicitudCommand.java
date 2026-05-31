package Application.Features.Auth.Commands.RejectRequest;

import Application.Abstractions.ICommand;

import java.util.UUID;

public record RechazarSolicitudCommand(
        UUID solicitudId,
        UUID adminId,
        String motivoRechazo
) implements ICommand<RechazarSolicitudResponse> {}
