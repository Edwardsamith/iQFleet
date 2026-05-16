package Infrastructure.Repositories;

import Domain.Entities.AssignmentHistory;
import Domain.Repositories.AssignmentHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAssignmentHistoryRepository
        extends GenericJpaRepository<AssignmentHistory>, AssignmentHistoryRepository {

    List<AssignmentHistory> findByVehicleId(UUID vehicleId);

    List<AssignmentHistory> findByDriverId(UUID driverId);

    Optional<AssignmentHistory> findByVehicleIdAndEndDateIsNull(UUID vehicleId);

    Optional<AssignmentHistory> findByDriverIdAndEndDateIsNull(UUID driverId);
}
