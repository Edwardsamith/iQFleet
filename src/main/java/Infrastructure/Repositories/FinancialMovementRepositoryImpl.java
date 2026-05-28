package Infrastructure.Repositories;

import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementType;
import Domain.Repositories.FinancialMovementRepository;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;
import Infrastructure.Persistence.Mappers.FinancialMovementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FinancialMovementRepositoryImpl implements FinancialMovementRepository {

    private final JpaFinancialMovementRepository jpaRepo;
    private final JpaVehicleRepository           jpaVehicleRepo;
    private final JpaDriverRepository            jpaDriverRepo;

    @Override
    public FinancialMovement save(FinancialMovement domain) {
        VehicleJpaEntity vehicleRef = jpaVehicleRepo.getReferenceById(domain.getVehicleId());
        DriverJpaEntity  driverRef  = domain.getDriverId() != null ? jpaDriverRepo.getReferenceById(domain.getDriverId()) : null;
        return FinancialMovementMapper.toDomain(jpaRepo.save(FinancialMovementMapper.toJpa(domain, vehicleRef, driverRef)));
    }

    @Override
    public Optional<FinancialMovement> findById(UUID id) {
        return jpaRepo.findById(id).map(FinancialMovementMapper::toDomain);
    }

    @Override
    public List<FinancialMovement> findAll() {
        return jpaRepo.findAll().stream().map(FinancialMovementMapper::toDomain).collect(Collectors.toList());
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
    public List<FinancialMovement> findByVehicleId(UUID vehicleId) {
        return jpaRepo.findByVehicle_Id(vehicleId).stream().map(FinancialMovementMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FinancialMovement> findByVehicleIdAndDateBetween(UUID vehicleId, LocalDate from, LocalDate to) {
        return jpaRepo.findByVehicle_IdAndDateBetween(vehicleId, from, to).stream().map(FinancialMovementMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FinancialMovement> findByMovementTypeAndVehicleId(MovementType movementType, UUID vehicleId) {
        return jpaRepo.findByMovementTypeAndVehicle_Id(movementType, vehicleId).stream().map(FinancialMovementMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumAmountByVehicleIdAndMovementTypeAndDateBetween(UUID vehicleId, MovementType movementType, LocalDate from, LocalDate to) {
        return jpaRepo.sumAmountByVehicleIdAndMovementTypeAndDateBetween(vehicleId, movementType, from, to);
    }
}
