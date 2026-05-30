package Infrastructure.Repositories;

import Domain.Enums.DriverStatus;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDriverRepository extends JpaRepository<DriverJpaEntity, UUID> {

    Optional<DriverJpaEntity> findByIdentificationNumber(String identificationNumber);

    Optional<DriverJpaEntity> findByLicenseNumber(String licenseNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<DriverJpaEntity> findByStatus(DriverStatus status);

    List<DriverJpaEntity> findByLicenseExpiryBefore(LocalDate date);
}
