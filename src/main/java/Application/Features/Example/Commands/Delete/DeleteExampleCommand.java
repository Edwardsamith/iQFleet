package Application.Features.Example.Commands.Delete;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class DeleteExampleCommand implements ICommand<Unit> {
    private @Getter @Setter UUID id;
}
