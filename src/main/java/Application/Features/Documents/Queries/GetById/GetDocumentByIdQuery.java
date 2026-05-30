package Application.Features.Documents.Queries.GetById;

import Application.Abstractions.IQuery;
import Domain.Entities.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetDocumentByIdQuery implements IQuery<Document> {

    private final UUID id;
}