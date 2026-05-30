package Application.Features.Documents.Commands.Renew;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RenewDocumentCommand implements ICommand<Unit> {

    private final UUID documentId;
    private final LocalDate newExpiryDate;
    private final String newFileUrl;
    private final String newFileFormat;
    private final String replacedBy;
}