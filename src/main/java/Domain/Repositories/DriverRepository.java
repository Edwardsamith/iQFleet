package Domain.Repositories;

import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DriverRepository extends Repository<Driver> {

    Optional<Driver> findByIdentificationNumber(String identificationNumber);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByLicenseExpiryBefore(LocalDate date);
}
