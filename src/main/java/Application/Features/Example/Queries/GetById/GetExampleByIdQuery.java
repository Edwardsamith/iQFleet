package Application.Features.Example.Queries.GetById;

import Application.Abstractions.IQuery;
import Application.Features.Example.Common.ExampleResponse;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GetExampleByIdQuery implements IQuery<ExampleResponse> {

    private final UUID id;

    public GetExampleByIdQuery(UUID id) {
        this.id = id;
    }
}
