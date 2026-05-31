package Domain.Repositories;

import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(UUID id);

    List<Vehicle> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByAssignedDriverIsNull();

    Optional<Vehicle> findByAssignedDriverId(UUID driverId);
}
