package Infrastructure.Repositories;

import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementType;
import Domain.Repositories.FinancialMovementRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFinancialMovementRepository
        extends GenericJpaRepository<FinancialMovement>, FinancialMovementRepository {

    List<FinancialMovement> findByVehicleId(UUID vehicleId);

    List<FinancialMovement> findByVehicleIdAndDateBetween(UUID vehicleId, LocalDate from, LocalDate to);

    List<FinancialMovement> findByMovementTypeAndVehicleId(MovementType movementType, UUID vehicleId);

    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM FinancialMovement m " +
           "WHERE m.vehicle.id = :vehicleId AND m.movementType = :movementType " +
           "AND m.date BETWEEN :from AND :to AND m.status = 'ACTIVE'")
    BigDecimal sumAmountByVehicleIdAndMovementTypeAndDateBetween(
            @Param("vehicleId") UUID vehicleId,
            @Param("movementType") MovementType movementType,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
