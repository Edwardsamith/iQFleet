package Application.Features.Finances.Commands.Cancel;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CancelMovementCommand implements ICommand<Unit> {
    private final UUID id;
}