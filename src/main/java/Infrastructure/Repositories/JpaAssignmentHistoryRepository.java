package Infrastructure.Repositories;

import Infrastructure.Persistence.Entities.AssignmentHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAssignmentHistoryRepository extends JpaRepository<AssignmentHistoryJpaEntity, UUID> {

    List<AssignmentHistoryJpaEntity> findByVehicle_Id(UUID vehicleId);

    List<AssignmentHistoryJpaEntity> findByDriver_Id(UUID driverId);

    Optional<AssignmentHistoryJpaEntity> findByVehicle_IdAndEndDateIsNull(UUID vehicleId);

    Optional<AssignmentHistoryJpaEntity> findByDriver_IdAndEndDateIsNull(UUID driverId);
}
