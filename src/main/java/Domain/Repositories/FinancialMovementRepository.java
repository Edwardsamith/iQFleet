package Domain.Repositories;

import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialMovementRepository {

    FinancialMovement save(FinancialMovement financialMovement);

    Optional<FinancialMovement> findById(UUID id);

    List<FinancialMovement> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    List<FinancialMovement> findByVehicleId(UUID vehicleId);

    List<FinancialMovement> findByVehicleIdAndDateBetween(UUID vehicleId, LocalDate from, LocalDate to);

    List<FinancialMovement> findByMovementTypeAndVehicleId(MovementType movementType, UUID vehicleId);

    BigDecimal sumAmountByVehicleIdAndMovementTypeAndDateBetween(UUID vehicleId, MovementType movementType, LocalDate from, LocalDate to);
}
