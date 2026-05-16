package Domain.Repositories;

import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends Repository<Vehicle> {

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByAssignedDriverIsNull();
}
