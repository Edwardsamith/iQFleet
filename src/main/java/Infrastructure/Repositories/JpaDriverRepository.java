package Infrastructure.Repositories;

import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;
import Domain.Repositories.DriverRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaDriverRepository extends GenericJpaRepository<Driver>, DriverRepository {

    Optional<Driver> findByIdentificationNumber(String identificationNumber);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByLicenseExpiryBefore(LocalDate date);
}
