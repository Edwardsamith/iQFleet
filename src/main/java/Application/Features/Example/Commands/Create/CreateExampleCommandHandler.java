package Application.Features.Example.Commands.Create;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Example;
import Domain.Repositories.ExampleRepository;
import java.util.ArrayList;

public class CreateExampleCommandHandler implements IRequestHandler<CreateExampleCommand, Unit> {

    private final ExampleRepository _exampleRepository;

    public CreateExampleCommandHandler(ExampleRepository exampleRepository) {
        _exampleRepository =  exampleRepository;
    }

    @Override
    public Result<Unit> handle(CreateExampleCommand request) {

        var errores = handleException(request);

        if(errores.size() > 0){
            return Result.Failure(errores);
        }

        try {
            Example example = new Example();
            example.setName(request.getName());
            _exampleRepository.save(example);
            return Result.Success();
        }catch (Exception e){
            return Result.Failure(e.getMessage());
        }
    }

    private ArrayList<String> handleException(CreateExampleCommand request) {
        ArrayList<String> errors = new ArrayList<>();

        if(request.getName() == null){
            errors.add("El nombre del example no puede ser nulo");
        }
        if(request.getApellido() == null){
            errors.add("El Apellido del example no puede ser nulo");
        }

        return errors;

    }
}
