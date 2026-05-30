package Application.Features.Example.Queries.GetAll;

import Application.Abstractions.IRequestHandler;
import Application.Features.Example.Common.ExampleResponse;
import Application.Result.Result;
import Domain.Repositories.ExampleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetAllExamplesQueryHandler implements IRequestHandler<GetAllExamplesQuery, List<ExampleResponse>> {

    private final ExampleRepository exampleRepository;

    public GetAllExamplesQueryHandler(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    @Override
    public Result<List<ExampleResponse>> handle(GetAllExamplesQuery request) {
        var examples = exampleRepository.findAll();
        List<ExampleResponse> response = examples.stream()
                .map(e -> new ExampleResponse(
                        e.getId(),
                        e.getName(),
                        e.getApellido(),
                        e.getCreatedAt(),
                        e.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        return Result.Success(response);
    }
}
