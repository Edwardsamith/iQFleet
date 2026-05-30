package Application.Features.Vehicles.Commands.ChangeStatus;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChangeVehicleStatusCommand implements ICommand<Unit> {

    private final UUID id;
    private final VehicleStatus status;
}