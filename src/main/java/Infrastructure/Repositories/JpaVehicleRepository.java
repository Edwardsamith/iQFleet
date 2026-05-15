package Infrastructure.Repositories;

import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.VehicleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaVehicleRepository extends GenericJpaRepository<Vehicle>, VehicleRepository {

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByAssignedDriverIsNull();
}
