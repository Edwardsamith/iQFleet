package Infrastructure.Repositories;

import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.VehicleRepository;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.UserJpaEntity;
import Infrastructure.Persistence.Mappers.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VehicleRepositoryImpl implements VehicleRepository {

    private final JpaVehicleRepository jpaRepo;
    private final JpaUserRepository    jpaUserRepo;
    private final JpaDriverRepository  jpaDriverRepo;

    @Override
    public Vehicle save(Vehicle domain) {
        UserJpaEntity   responsible    = domain.getResponsibleId()    != null ? jpaUserRepo.getReferenceById(domain.getResponsibleId())   : null;
        DriverJpaEntity assignedDriver = domain.getAssignedDriverId() != null ? jpaDriverRepo.getReferenceById(domain.getAssignedDriverId()) : null;
        return VehicleMapper.toDomain(jpaRepo.save(VehicleMapper.toJpa(domain, responsible, assignedDriver)));
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return jpaRepo.findById(id).map(VehicleMapper::toDomain);
    }

    @Override
    public List<Vehicle> findAll() {
        return jpaRepo.findAll().stream().map(VehicleMapper::toDomain).collect(Collectors.toList());
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
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return jpaRepo.findByPlateNumber(plateNumber).map(VehicleMapper::toDomain);
    }

    @Override
    public boolean existsByPlateNumber(String plateNumber) {
        return jpaRepo.existsByPlateNumber(plateNumber);
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) {
        return jpaRepo.findByStatus(status).stream().map(VehicleMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> findByAssignedDriverIsNull() {
        return jpaRepo.findByAssignedDriverIsNull().stream().map(VehicleMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicle> findByAssignedDriverId(UUID driverId) {
        return jpaRepo.findByAssignedDriver_Id(driverId).map(VehicleMapper::toDomain);
    }
}
