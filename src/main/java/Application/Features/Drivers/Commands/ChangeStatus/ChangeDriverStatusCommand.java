package Application.Features.Drivers.Commands.ChangeStatus;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChangeDriverStatusCommand implements ICommand<Unit> {

    private final UUID id;
    private final DriverStatus status;
}