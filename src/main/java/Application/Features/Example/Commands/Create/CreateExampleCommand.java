package Application.Features.Example.Commands.Create;


import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class CreateExampleCommand implements ICommand<Unit> {
    private @Getter  @Setter String name;
    private @Getter  @Setter String apellido;

    public CreateExampleCommand(String name, String apellido){
        this.name = name;
        this.apellido = apellido;
    }
}

