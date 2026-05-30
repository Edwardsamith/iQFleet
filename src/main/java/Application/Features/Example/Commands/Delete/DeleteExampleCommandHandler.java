package Application.Features.Example.Commands.Delete;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Repositories.ExampleRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteExampleCommandHandler implements IRequestHandler<DeleteExampleCommand, Unit> {

    private final ExampleRepository exampleRepository;

    public DeleteExampleCommandHandler(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public Result<Unit> handle(DeleteExampleCommand request) {
        try {
            if (!exampleRepository.existsById(request.getId())) {
                return Result.Failure("Example no encontrado con id: " + request.getId());
            }
            exampleRepository.deleteById(request.getId());
            return Result.Success();
        } catch (Exception e) {
            return Result.Failure(e.getMessage());
        }
    }
}
