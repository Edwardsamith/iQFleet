package Application.Features.Documents.Queries.GetAlerts;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetDocumentAlertsQueryHandler
        implements IRequestHandler<GetDocumentAlertsQuery, List<Document>> {

    private final DocumentRepository documentRepository;

    @Override
    public Result<List<Document>> handle(GetDocumentAlertsQuery query) {

        int days = query.getAlertDays() != null ? query.getAlertDays() : 30;
        LocalDate limit = LocalDate.now().plusDays(days);

        // Trae documentos vencidos y próximos a vencer
        // Excluye los inactivos y los sin vencimiento
        List<Document> alerts = documentRepository.findAll().stream()
                .filter(d -> d.getStatus() != DocumentStatus.INACTIVE
                        && d.getStatus() != DocumentStatus.NO_EXPIRY
                        && d.getExpiryDate() != null
                        && !d.getExpiryDate().isAfter(limit))
                .collect(Collectors.toList());

        return Result.Success(alerts);
    }
}