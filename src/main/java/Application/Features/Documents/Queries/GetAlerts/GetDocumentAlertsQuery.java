package Application.Features.Documents.Queries.GetAlerts;

import Application.Abstractions.IQuery;
import Domain.Entities.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetDocumentAlertsQuery implements IQuery<List<Document>> {

    // Días de anticipación para alertas (default 30 según RF-003)
    private final Integer alertDays;
}