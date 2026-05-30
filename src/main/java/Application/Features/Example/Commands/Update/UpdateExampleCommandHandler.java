package Application.Features.Example.Commands.Update;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Repositories.ExampleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UpdateExampleCommandHandler implements IRequestHandler<UpdateExampleCommand, Unit> {

    private final ExampleRepository exampleRepository;

    public UpdateExampleCommandHandler(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public Result<Unit> handle(UpdateExampleCommand request) {
        var errors = validate(request);
        if (!errors.isEmpty()) {
            return Result.Failure(errors);
        }

        try {
            var exampleOpt = exampleRepository.findById(request.getId());
            if (exampleOpt.isEmpty()) {
                return Result.Failure("Example no encontrado con id: " + request.getId());
            }

            var example = exampleOpt.get();
            example.setName(request.getName());
            example.setApellido(request.getApellido());
            exampleRepository.save(example);
            return Result.Success();
        } catch (Exception e) {
            return Result.Failure(e.getMessage());
        }
    }

    private ArrayList<String> validate(UpdateExampleCommand request) {
        ArrayList<String> errors = new ArrayList<>();
        if (request.getId() == null) {
            errors.add("El id no puede ser nulo");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            errors.add("El nombre no puede ser nulo o vacío");
        }
        if (request.getApellido() == null || request.getApellido().isBlank()) {
            errors.add("El apellido no puede ser nulo o vacío");
        }
        return errors;
    }
}
