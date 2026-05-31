package Application.Features.Auth.Commands.ApproveRequest;

import Application.Abstractions.ICommand;
import Domain.Enums.Role;

import java.util.UUID;

public record AprobarSolicitudCommand(
        UUID solicitudId,
        UUID adminId,
        Role rolAsignado
) implements ICommand<AprobarSolicitudResponse> {}
