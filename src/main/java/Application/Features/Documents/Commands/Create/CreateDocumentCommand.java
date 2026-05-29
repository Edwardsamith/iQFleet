package Application.Features.Documents.Commands.Create;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateDocumentCommand implements ICommand<Unit> {

    private final String name;
    private final DocumentType documentType;
    private final String referenceNumber;
    private final String issuingEntity;
    private final LocalDate issueDate;
    private final LocalDate expiryDate;       // null = sin vencimiento
    private final String fileUrl;
    private final String fileFormat;
    private final String notes;
    private final UUID driverId;              // null si es de vehículo
    private final UUID vehicleId;             // null si es de conductor
    private final String uploadedBy;
}