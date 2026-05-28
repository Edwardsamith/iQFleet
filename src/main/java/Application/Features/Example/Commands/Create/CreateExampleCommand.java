package Application.Features.Example.Commands.Create;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.Getter;

@Getter
public class CreateExampleCommand implements ICommand<Unit> {

    private final String name;
    private final String apellido;

    public CreateExampleCommand(String name, String apellido) {
        this.name = name;
        this.apellido = apellido;
    }
}
