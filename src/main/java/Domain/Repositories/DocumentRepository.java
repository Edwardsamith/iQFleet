package Domain.Repositories;

import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends Repository<Document> {

    List<Document> findByDriverId(UUID driverId);

    List<Document> findByVehicleId(UUID vehicleId);

    List<Document> findByStatus(DocumentStatus status);

    List<Document> findByExpiryDateBeforeAndStatusNot(LocalDate date, DocumentStatus status);

    List<Document> findByDocumentTypeAndVehicleId(DocumentType documentType, UUID vehicleId);

    List<Document> findByDocumentTypeAndDriverId(DocumentType documentType, UUID driverId);
}
