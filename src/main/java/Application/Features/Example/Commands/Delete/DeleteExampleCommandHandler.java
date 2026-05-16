package Application.Features.Example.Commands.Delete;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Repositories.ExampleRepository;

public class DeleteExampleCommandHandler implements IRequestHandler<DeleteExampleCommand, Unit> {

    ExampleRepository _exampleRepository;

    public DeleteExampleCommandHandler(ExampleRepository exampleRepository) {
        _exampleRepository = exampleRepository;
    }

    public Result<Unit> handle(DeleteExampleCommand request) {
        try {
            _exampleRepository.delete(request.getId());
            return Result.Success();
        }catch (Exception e) {
            return Result.Failure(e.getMessage());
        }
    };
}
