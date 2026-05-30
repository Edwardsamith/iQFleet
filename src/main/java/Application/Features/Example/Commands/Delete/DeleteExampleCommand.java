package Application.Features.Example.Commands.Delete;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteExampleCommand implements ICommand<Unit> {

    private final UUID id;

    public DeleteExampleCommand(UUID id) {
        this.id = id;
    }
}
