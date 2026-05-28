package Application.Features.Example.Queries.GetById;

import Application.Abstractions.IRequestHandler;
import Application.Features.Example.Common.ExampleResponse;
import Application.Result.Result;
import Domain.Repositories.ExampleRepository;
import org.springframework.stereotype.Component;

@Component
public class GetExampleByIdQueryHandler implements IRequestHandler<GetExampleByIdQuery, ExampleResponse> {

    private final ExampleRepository exampleRepository;

    public GetExampleByIdQueryHandler(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public Result<ExampleResponse> handle(GetExampleByIdQuery request) {
        var exampleOpt = exampleRepository.findById(request.getId());
        if (exampleOpt.isEmpty()) {
            return Result.Failure("Example no encontrado con id: " + request.getId());
        }

        var example = exampleOpt.get();
        return Result.Success(new ExampleResponse(
                example.getId(),
                example.getName(),
                example.getApellido(),
                example.getCreatedAt(),
                example.getUpdatedAt()
        ));
    }
}
