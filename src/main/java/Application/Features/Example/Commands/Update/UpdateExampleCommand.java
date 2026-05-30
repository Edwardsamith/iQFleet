package Application.Features.Example.Commands.Update;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateExampleCommand implements ICommand<Unit> {

    private final UUID id;
    private final String name;
    private final String apellido;

    public UpdateExampleCommand(UUID id, String name, String apellido) {
        this.id = id;
        this.name = name;
        this.apellido = apellido;
    }
}
