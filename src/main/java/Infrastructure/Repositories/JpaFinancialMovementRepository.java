package Infrastructure.Repositories;

import Domain.Enums.MovementType;
import Infrastructure.Persistence.Entities.FinancialMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFinancialMovementRepository extends JpaRepository<FinancialMovementJpaEntity, UUID> {

    List<FinancialMovementJpaEntity> findByVehicle_Id(UUID vehicleId);

    List<FinancialMovementJpaEntity> findByVehicle_IdAndDateBetween(UUID vehicleId, LocalDate from, LocalDate to);

    List<FinancialMovementJpaEntity> findByMovementTypeAndVehicle_Id(MovementType movementType, UUID vehicleId);

    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM FinancialMovementJpaEntity m " +
           "WHERE m.vehicle.id = :vehicleId AND m.movementType = :movementType " +
           "AND m.date BETWEEN :from AND :to AND m.status = 'ACTIVE'")
    BigDecimal sumAmountByVehicleIdAndMovementTypeAndDateBetween(
            @Param("vehicleId") UUID vehicleId,
            @Param("movementType") MovementType movementType,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
