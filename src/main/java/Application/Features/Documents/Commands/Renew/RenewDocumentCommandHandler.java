package Application.Features.Documents.Commands.Renew;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Document;
import Domain.Entities.DocumentVersion;
import Domain.Repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RenewDocumentCommandHandler
        implements IRequestHandler<RenewDocumentCommand, Unit> {

    private final DocumentRepository documentRepository;

    @Override
    public Result<Unit> handle(RenewDocumentCommand command) {

        Document document = documentRepository.findById(command.getDocumentId())
                .orElse(null);

        if (document == null) {
            return Result.Failure("No se encontró el documento");
        }

        // Regla de negocio: no se puede renovar un documento inactivo
        if (document.getStatus() == Domain.Enums.DocumentStatus.INACTIVE) {
            return Result.Failure("No se puede renovar un documento inactivo");
        }

        // Regla de negocio: la nueva fecha debe ser posterior a la actual
        if (document.getExpiryDate() != null &&
                command.getNewExpiryDate().isBefore(document.getExpiryDate())) {
            return Result.Failure("La nueva fecha de vencimiento debe ser posterior a la actual");
        }

        // Guarda la versión anterior en el historial
        DocumentVersion version = DocumentVersion.builder()
                .documentId(document.getId())
                .fileUrl(document.getFileUrl())
                .previousExpiryDate(document.getExpiryDate())
                .replacedBy(command.getReplacedBy())
                .replacedAt(LocalDateTime.now())
                .build();

        document.getVersions().add(version);

        // Actualiza el documento con los nuevos datos
        document.setExpiryDate(command.getNewExpiryDate());
        document.setFileUrl(command.getNewFileUrl());
        document.setFileFormat(command.getNewFileFormat());

        documentRepository.update(document);

        return Result.Success();
    }
}
