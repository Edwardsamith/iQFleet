package Console;

import Application.Features.Example.Commands.Create.CreateExampleCommand;
import Application.Features.Example.Commands.Create.CreateExampleCommandHandler;
import Application.Features.Example.Commands.Delete.DeleteExampleCommand;
import Application.Features.Example.Commands.Delete.DeleteExampleCommandHandler;
import Application.Mediator;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Repositories.ExampleRepository;
import Infrastructure.Repositories.FileExampleRepository;

public class Main {
    public static void main(String[] args) {

        Mediator mediator = new Mediator();
        ExampleRepository repo = new FileExampleRepository();
        mediator.registerHandler(CreateExampleCommand.class, new CreateExampleCommandHandler(repo));
        mediator.registerHandler(DeleteExampleCommand.class, new DeleteExampleCommandHandler(repo));


        Result<Unit> resulCreate = mediator.send(new CreateExampleCommand("Eduardo", "Ramirez"));

        if(!resulCreate.isSuccess()){
            for (String error : resulCreate.getErrors()){
                System.out.println(error);
            }
        }else {
            System.out.println("Guardado");
        }


    }
}
