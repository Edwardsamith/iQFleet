package Application.Features.Documents.Commands.ChangeStatus;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangeDocumentStatusCommandHandler
        implements IRequestHandler<ChangeDocumentStatusCommand, Unit> {

    private final DocumentRepository documentRepository;

    @Override
    public Result<Unit> handle(ChangeDocumentStatusCommand command) {

        Document document = documentRepository.findById(command.getDocumentId())
                .orElse(null);

        if (document == null) {
            return Result.Failure("No se encontró el documento");
        }

        // Regla de negocio: solo se puede inactivar, no reactivar manualmente
        if (document.getStatus() == DocumentStatus.INACTIVE) {
            return Result.Failure("El documento ya está inactivo");
        }

        document.setStatus(DocumentStatus.INACTIVE);
        documentRepository.update(document);

        return Result.Success();
    }
}