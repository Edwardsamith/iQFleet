package Infrastructure.Persistence.Mappers;

import Domain.Entities.Vehicle;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.UserJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;

public class VehicleMapper {

    public static Vehicle toDomain(VehicleJpaEntity jpa) {
        Vehicle domain = new Vehicle();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setPlateNumber(jpa.getPlateNumber());
        domain.setBrand(jpa.getBrand());
        domain.setVehicleModel(jpa.getVehicleModel());
        domain.setVehicleType(jpa.getVehicleType());
        domain.setYear(jpa.getYear());
        domain.setStatus(jpa.getStatus());
        domain.setRegistrationDate(jpa.getRegistrationDate());
        domain.setNotes(jpa.getNotes());
        if (jpa.getResponsible() != null)     domain.setResponsibleId(jpa.getResponsible().getId());
        if (jpa.getAssignedDriver() != null)  domain.setAssignedDriverId(jpa.getAssignedDriver().getId());
        return domain;
    }

    public static VehicleJpaEntity toJpa(Vehicle domain,
                                          UserJpaEntity responsibleRef,
                                          DriverJpaEntity assignedDriverRef) {
        VehicleJpaEntity jpa = new VehicleJpaEntity();
        jpa.setId(domain.getId());
        jpa.setPlateNumber(domain.getPlateNumber());
        jpa.setBrand(domain.getBrand());
        jpa.setVehicleModel(domain.getVehicleModel());
        jpa.setVehicleType(domain.getVehicleType());
        jpa.setYear(domain.getYear());
        jpa.setStatus(domain.getStatus());
        jpa.setRegistrationDate(domain.getRegistrationDate());
        jpa.setNotes(domain.getNotes());
        jpa.setResponsible(responsibleRef);
        jpa.setAssignedDriver(assignedDriverRef);
        return jpa;
    }
}
