package Application.Features.Example.Commands.Create;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Example;
import Domain.Repositories.ExampleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CreateExampleCommandHandler implements IRequestHandler<CreateExampleCommand, Unit> {

    private final ExampleRepository exampleRepository;

    public CreateExampleCommandHandler(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public Result<Unit> handle(CreateExampleCommand request) {

        var errors = validate(request);

        if (!errors.isEmpty()) {
            return Result.Failure(errors);
        }

        try {


            Example example = Example.builder()
                    .name(request.getName())
                    .apellido(request.getApellido())
                    .build();

            example.setName(request.getName());
            example.setApellido(request.getApellido());


            exampleRepository.save(example);


            return Result.Success();

        } catch (Exception e) {
            return Result.Failure(e.getMessage());
        }
    }

    private ArrayList<String> validate(CreateExampleCommand request) {
        ArrayList<String> errors = new ArrayList<>();
        if (request.getName() == null || request.getName().isBlank()) {
            errors.add("El nombre no puede ser nulo o vacío");
        }
        if (request.getApellido() == null || request.getApellido().isBlank()) {
            errors.add("El apellido no puede ser nulo o vacío");
        }
        return errors;
    }
}
