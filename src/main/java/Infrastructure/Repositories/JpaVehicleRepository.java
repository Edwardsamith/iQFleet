package Infrastructure.Repositories;

import Domain.Enums.VehicleStatus;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaVehicleRepository extends JpaRepository<VehicleJpaEntity, UUID> {

    Optional<VehicleJpaEntity> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);

    List<VehicleJpaEntity> findByStatus(VehicleStatus status);

    List<VehicleJpaEntity> findByAssignedDriverIsNull();
}
