package Infrastructure.Repositories;

import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import Domain.Repositories.DocumentRepository;
import Infrastructure.Persistence.Entities.DocumentJpaEntity;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;
import Infrastructure.Persistence.Mappers.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final JpaDocumentRepository jpaRepo;
    private final JpaDriverRepository   jpaDriverRepo;
    private final JpaVehicleRepository  jpaVehicleRepo;

    @Override
    public Document save(Document domain) {
        DriverJpaEntity  driverRef  = domain.getDriverId()  != null ? jpaDriverRepo.getReferenceById(domain.getDriverId())   : null;
        VehicleJpaEntity vehicleRef = domain.getVehicleId() != null ? jpaVehicleRepo.getReferenceById(domain.getVehicleId()) : null;
        DocumentJpaEntity saved = jpaRepo.save(DocumentMapper.toJpa(domain, driverRef, vehicleRef));
        return DocumentMapper.toDomain(saved);
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaRepo.findById(id).map(DocumentMapper::toDomain);
    }

    @Override
    public List<Document> findAll() {
        return jpaRepo.findAll().stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepo.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepo.deleteById(id);
    }

    @Override
    public List<Document> findByDriverId(UUID driverId) {
        return jpaRepo.findByDriver_Id(driverId).stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Document> findByVehicleId(UUID vehicleId) {
        return jpaRepo.findByVehicle_Id(vehicleId).stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Document> findByStatus(DocumentStatus status) {
        return jpaRepo.findByStatus(status).stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Document> findByExpiryDateBeforeAndStatusNot(LocalDate date, DocumentStatus status) {
        return jpaRepo.findByExpiryDateBeforeAndStatusNot(date, status).stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Document> findByDocumentTypeAndVehicleId(DocumentType documentType, UUID vehicleId) {
        return jpaRepo.findByDocumentTypeAndVehicle_Id(documentType, vehicleId).stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Document> findByDocumentTypeAndDriverId(DocumentType documentType, UUID driverId) {
        return jpaRepo.findByDocumentTypeAndDriver_Id(documentType, driverId).stream().map(DocumentMapper::toDomain).collect(Collectors.toList());
    }
}
