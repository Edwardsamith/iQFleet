package Infrastructure.Repositories;

import Domain.Entities.AssignmentHistory;
import Domain.Repositories.AssignmentHistoryRepository;
import Infrastructure.Persistence.Entities.AssignmentHistoryJpaEntity;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;
import Infrastructure.Persistence.Mappers.AssignmentHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AssignmentHistoryRepositoryImpl implements AssignmentHistoryRepository {

    private final JpaAssignmentHistoryRepository jpaRepo;
    private final JpaVehicleRepository           jpaVehicleRepo;
    private final JpaDriverRepository            jpaDriverRepo;

    @Override
    public AssignmentHistory save(AssignmentHistory domain) {
        VehicleJpaEntity vehicleRef = jpaVehicleRepo.getReferenceById(domain.getVehicleId());
        DriverJpaEntity  driverRef  = jpaDriverRepo.getReferenceById(domain.getDriverId());
        AssignmentHistoryJpaEntity saved = jpaRepo.save(AssignmentHistoryMapper.toJpa(domain, vehicleRef, driverRef));
        return AssignmentHistoryMapper.toDomain(saved);
    }

    @Override
    public Optional<AssignmentHistory> findById(UUID id) {
        return jpaRepo.findById(id).map(AssignmentHistoryMapper::toDomain);
    }

    @Override
    public List<AssignmentHistory> findAll() {
        return jpaRepo.findAll().stream().map(AssignmentHistoryMapper::toDomain).collect(Collectors.toList());
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
    public List<AssignmentHistory> findByVehicleId(UUID vehicleId) {
        return jpaRepo.findByVehicle_Id(vehicleId).stream().map(AssignmentHistoryMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AssignmentHistory> findByDriverId(UUID driverId) {
        return jpaRepo.findByDriver_Id(driverId).stream().map(AssignmentHistoryMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<AssignmentHistory> findByVehicleIdAndEndDateIsNull(UUID vehicleId) {
        return jpaRepo.findByVehicle_IdAndEndDateIsNull(vehicleId).map(AssignmentHistoryMapper::toDomain);
    }

    @Override
    public Optional<AssignmentHistory> findByDriverIdAndEndDateIsNull(UUID driverId) {
        return jpaRepo.findByDriver_IdAndEndDateIsNull(driverId).map(AssignmentHistoryMapper::toDomain);
    }
}
