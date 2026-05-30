package Application.Features.Vehicles.Commands.AssignDriver;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AssignDriverCommand implements ICommand<Unit> {

    private final UUID vehicleId;
    private final UUID driverId; // null = desasignar
}
