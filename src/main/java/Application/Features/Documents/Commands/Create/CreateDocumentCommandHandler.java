package Application.Features.Documents.Commands.Create;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Document;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DocumentType;
import Domain.Repositories.DocumentRepository;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateDocumentCommandHandler
        implements IRequestHandler<CreateDocumentCommand, Unit> {

    private final DocumentRepository documentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Result<Unit> handle(CreateDocumentCommand command) {

        // Regla de negocio: debe pertenecer a conductor O vehículo, nunca a ambos ni a ninguno
        if (command.getDriverId() == null && command.getVehicleId() == null) {
            return Result.Failure("El documento debe estar asociado a un conductor o un vehículo");
        }

        if (command.getDriverId() != null && command.getVehicleId() != null) {
            return Result.Failure("El documento no puede estar asociado a un conductor y un vehículo al mismo tiempo");
        }

        // Valida que el tipo de documento corresponda a la entidad
        if (command.getDriverId() != null) {
            if (!isDriverDocumentType(command.getDocumentType())) {
                return Result.Failure("El tipo de documento no corresponde a un conductor");
            }
        }

        if (command.getVehicleId() != null) {
            if (!isVehicleDocumentType(command.getDocumentType())) {
                return Result.Failure("El tipo de documento no corresponde a un vehículo");
            }
        }

        // Busca conductor si aplica
        Driver driver = null;
        if (command.getDriverId() != null) {
            driver = driverRepository.findById(command.getDriverId())
                    .orElse(null);
            if (driver == null) {
                return Result.Failure("No se encontró el conductor");
            }
        }

        // Busca vehículo si aplica
        Vehicle vehicle = null;
        if (command.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(command.getVehicleId())
                    .orElse(null);
            if (vehicle == null) {
                return Result.Failure("No se encontró el vehículo");
            }
        }

        // Valida que la fecha de emisión no sea futura
        if (command.getIssueDate() != null &&
                command.getIssueDate().isAfter(java.time.LocalDate.now())) {
            return Result.Failure("La fecha de emisión no puede ser futura");
        }

        // Valida que la fecha de vencimiento sea posterior a la de emisión
        if (command.getIssueDate() != null && command.getExpiryDate() != null &&
                command.getExpiryDate().isBefore(command.getIssueDate())) {
            return Result.Failure("La fecha de vencimiento no puede ser anterior a la fecha de emisión");
        }

        Document document = Document.builder()
                .name(command.getName())
                .documentType(command.getDocumentType())
                .referenceNumber(command.getReferenceNumber())
                .issuingEntity(command.getIssuingEntity())
                .issueDate(command.getIssueDate())
                .expiryDate(command.getExpiryDate())
                .fileUrl(command.getFileUrl())
                .fileFormat(command.getFileFormat())
                .notes(command.getNotes())
                .driverId(command.getDriverId())
                .vehicleId(command.getVehicleId())
                .uploadedBy(command.getUploadedBy())
                .uploadDate(LocalDateTime.now())
                .build();

        documentRepository.save(document);

        return Result.Success();
    }

    private boolean isDriverDocumentType(DocumentType type) {
        return type == DocumentType.DRIVING_LICENSE
                || type == DocumentType.MEDICAL_CERTIFICATE
                || type == DocumentType.EMPLOYMENT_CONTRACT
                || type == DocumentType.TRAFFIC_CLEARANCE
                || type == DocumentType.OTHER_DRIVER;
    }

    private boolean isVehicleDocumentType(DocumentType type) {
        return type == DocumentType.SOAT
                || type == DocumentType.RTM
                || type == DocumentType.OPERATION_CARD
                || type == DocumentType.PROPERTY_CARD
                || type == DocumentType.LIABILITY_POLICY
                || type == DocumentType.OTHER_VEHICLE
                || type == DocumentType.COMPANY_LICENSE
                || type == DocumentType.ROUTE_CONTRACT
                || type == DocumentType.OTHER_ADMIN;
    }
}