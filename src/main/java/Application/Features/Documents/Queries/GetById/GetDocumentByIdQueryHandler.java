package Application.Features.Documents.Queries.GetById;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Document;
import Domain.Repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetDocumentByIdQueryHandler
        implements IRequestHandler<GetDocumentByIdQuery, Document> {

    private final DocumentRepository documentRepository;

    @Override
    public Result<Document> handle(GetDocumentByIdQuery query) {

        Document document = documentRepository.findById(query.getId())
                .orElse(null);

        if (document == null) {
            return Result.Failure("No se encontró el documento");
        }

        return Result.Success(document);
    }
}