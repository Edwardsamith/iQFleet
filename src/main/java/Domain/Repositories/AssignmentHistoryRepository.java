package Domain.Repositories;

import Domain.Entities.AssignmentHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentHistoryRepository {

    AssignmentHistory save(AssignmentHistory assignmentHistory);

    Optional<AssignmentHistory> findById(UUID id);

    List<AssignmentHistory> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    List<AssignmentHistory> findByVehicleId(UUID vehicleId);

    List<AssignmentHistory> findByDriverId(UUID driverId);

    Optional<AssignmentHistory> findByVehicleIdAndEndDateIsNull(UUID vehicleId);

    Optional<AssignmentHistory> findByDriverIdAndEndDateIsNull(UUID driverId);
}
