package Infrastructure.Persistence.Mappers;

import Domain.Entities.AssignmentHistory;
import Infrastructure.Persistence.Entities.AssignmentHistoryJpaEntity;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;

public class AssignmentHistoryMapper {

    public static AssignmentHistory toDomain(AssignmentHistoryJpaEntity jpa) {
        AssignmentHistory domain = new AssignmentHistory();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setVehicleId(jpa.getVehicle().getId());
        domain.setDriverId(jpa.getDriver().getId());
        domain.setStartDate(jpa.getStartDate());
        domain.setEndDate(jpa.getEndDate());
        return domain;
    }

    public static AssignmentHistoryJpaEntity toJpa(AssignmentHistory domain,
                                                    VehicleJpaEntity vehicleRef,
                                                    DriverJpaEntity driverRef) {
        AssignmentHistoryJpaEntity jpa = new AssignmentHistoryJpaEntity();
        jpa.setId(domain.getId());
        jpa.setVehicle(vehicleRef);
        jpa.setDriver(driverRef);
        jpa.setStartDate(domain.getStartDate());
        jpa.setEndDate(domain.getEndDate());
        return jpa;
    }
}
