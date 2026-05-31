package Domain.Repositories;

import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository {

    Driver save(Driver driver);

    Driver update(Driver driver);

    Optional<Driver> findById(UUID id);

    List<Driver> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<Driver> findByIdentificationNumber(String identificationNumber);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByLicenseExpiryBefore(LocalDate date);
}
