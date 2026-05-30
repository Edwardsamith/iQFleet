package Application.Features.Documents.Queries.GetByFilter;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Document;
import Domain.Repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetDocumentsByFilterQueryHandler
        implements IRequestHandler<GetDocumentsByFilterQuery, List<Document>> {

    private final DocumentRepository documentRepository;

    @Override
    public Result<List<Document>> handle(GetDocumentsByFilterQuery query) {

        List<Document> documents = documentRepository.findAll();

        if (query.getDriverId() != null) {
            documents = documents.stream()
                    .filter(d -> d.getDriver() != null &&
                            d.getDriver().getId().equals(query.getDriverId()))
                    .collect(Collectors.toList());
        }

        if (query.getVehicleId() != null) {
            documents = documents.stream()
                    .filter(d -> d.getVehicle() != null &&
                            d.getVehicle().getId().equals(query.getVehicleId()))
                    .collect(Collectors.toList());
        }

        if (query.getType() != null) {
            documents = documents.stream()
                    .filter(d -> d.getDocumentType() == query.getType())
                    .collect(Collectors.toList());
        }

        if (query.getStatus() != null) {
            documents = documents.stream()
                    .filter(d -> d.getStatus() == query.getStatus())
                    .collect(Collectors.toList());
        }

        return Result.Success(documents);
    }
}