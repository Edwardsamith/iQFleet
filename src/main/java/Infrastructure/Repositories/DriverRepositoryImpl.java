package Infrastructure.Repositories;

import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;
import Domain.Repositories.DriverRepository;
import Infrastructure.Persistence.Mappers.DriverMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DriverRepositoryImpl implements DriverRepository {

    private final JpaDriverRepository jpaRepo;

    @Override
    public Driver save(Driver domain) {
        return DriverMapper.toDomain(jpaRepo.save(DriverMapper.toJpa(domain)));
    }

    @Override
    public Optional<Driver> findById(UUID id) {
        return jpaRepo.findById(id).map(DriverMapper::toDomain);
    }

    @Override
    public List<Driver> findAll() {
        return jpaRepo.findAll().stream().map(DriverMapper::toDomain).collect(Collectors.toList());
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
    public Optional<Driver> findByIdentificationNumber(String identificationNumber) {
        return jpaRepo.findByIdentificationNumber(identificationNumber).map(DriverMapper::toDomain);
    }

    @Override
    public Optional<Driver> findByLicenseNumber(String licenseNumber) {
        return jpaRepo.findByLicenseNumber(licenseNumber).map(DriverMapper::toDomain);
    }

    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        return jpaRepo.existsByIdentificationNumber(identificationNumber);
    }

    @Override
    public boolean existsByLicenseNumber(String licenseNumber) {
        return jpaRepo.existsByLicenseNumber(licenseNumber);
    }

    @Override
    public List<Driver> findByStatus(DriverStatus status) {
        return jpaRepo.findByStatus(status).stream().map(DriverMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Driver> findByLicenseExpiryBefore(LocalDate date) {
        return jpaRepo.findByLicenseExpiryBefore(date).stream().map(DriverMapper::toDomain).collect(Collectors.toList());
    }
}
