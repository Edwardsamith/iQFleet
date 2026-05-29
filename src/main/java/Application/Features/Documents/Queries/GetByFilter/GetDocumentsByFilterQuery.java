package Application.Features.Documents.Queries.GetByFilter;

import Application.Abstractions.IQuery;
import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetDocumentsByFilterQuery implements IQuery<List<Document>> {

    private final UUID driverId;        // null = todos
    private final UUID vehicleId;       // null = todos
    private final DocumentType type;    // null = todos
    private final DocumentStatus status;// null = todos
}