package Infrastructure.Repositories;

import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import Infrastructure.Persistence.Entities.DocumentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaDocumentRepository extends JpaRepository<DocumentJpaEntity, UUID> {

    List<DocumentJpaEntity> findByDriver_Id(UUID driverId);

    List<DocumentJpaEntity> findByVehicle_Id(UUID vehicleId);

    List<DocumentJpaEntity> findByStatus(DocumentStatus status);

    List<DocumentJpaEntity> findByExpiryDateBeforeAndStatusNot(LocalDate date, DocumentStatus status);

    List<DocumentJpaEntity> findByDocumentTypeAndVehicle_Id(DocumentType documentType, UUID vehicleId);

    List<DocumentJpaEntity> findByDocumentTypeAndDriver_Id(DocumentType documentType, UUID driverId);
}
