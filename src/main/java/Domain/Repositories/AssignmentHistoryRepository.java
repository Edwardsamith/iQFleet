package Domain.Repositories;

import Domain.Entities.AssignmentHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentHistoryRepository extends Repository<AssignmentHistory> {

    List<AssignmentHistory> findByVehicleId(UUID vehicleId);

    List<AssignmentHistory> findByDriverId(UUID driverId);

    Optional<AssignmentHistory> findByVehicleIdAndEndDateIsNull(UUID vehicleId);

    Optional<AssignmentHistory> findByDriverIdAndEndDateIsNull(UUID driverId);
}
