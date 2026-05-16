package Infrastructure.Repositories;

import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import Domain.Repositories.DocumentRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaDocumentRepository extends GenericJpaRepository<Document>, DocumentRepository {

    List<Document> findByDriverId(UUID driverId);

    List<Document> findByVehicleId(UUID vehicleId);

    List<Document> findByStatus(DocumentStatus status);

    List<Document> findByExpiryDateBeforeAndStatusNot(LocalDate date, DocumentStatus status);

    List<Document> findByDocumentTypeAndVehicleId(DocumentType documentType, UUID vehicleId);

    List<Document> findByDocumentTypeAndDriverId(DocumentType documentType, UUID driverId);
}
