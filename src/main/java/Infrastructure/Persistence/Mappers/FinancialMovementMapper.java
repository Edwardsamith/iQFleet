package Infrastructure.Persistence.Mappers;

import Domain.Entities.FinancialMovement;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.FinancialMovementJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;

public class FinancialMovementMapper {

    public static FinancialMovement toDomain(FinancialMovementJpaEntity jpa) {
        FinancialMovement domain = new FinancialMovement();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setMovementType(jpa.getMovementType());
        domain.setCategory(jpa.getCategory());
        domain.setPaymentMethod(jpa.getPaymentMethod());
        domain.setAmount(jpa.getAmount());
        domain.setDate(jpa.getDate());
        domain.setDescription(jpa.getDescription());
        domain.setNotes(jpa.getNotes());
        domain.setStatus(jpa.getStatus());
        domain.setRegisteredBy(jpa.getRegisteredBy());
        domain.setVehicleId(jpa.getVehicle().getId());
        if (jpa.getDriver() != null) domain.setDriverId(jpa.getDriver().getId());
        return domain;
    }

    public static FinancialMovementJpaEntity toJpa(FinancialMovement domain,
                                                    VehicleJpaEntity vehicleRef,
                                                    DriverJpaEntity driverRef) {
        FinancialMovementJpaEntity jpa = new FinancialMovementJpaEntity();
        jpa.setId(domain.getId());
        jpa.setMovementType(domain.getMovementType());
        jpa.setCategory(domain.getCategory());
        jpa.setPaymentMethod(domain.getPaymentMethod());
        jpa.setAmount(domain.getAmount());
        jpa.setDate(domain.getDate());
        jpa.setDescription(domain.getDescription());
        jpa.setNotes(domain.getNotes());
        jpa.setStatus(domain.getStatus());
        jpa.setRegisteredBy(domain.getRegisteredBy());
        jpa.setVehicle(vehicleRef);
        jpa.setDriver(driverRef);
        return jpa;
    }
}
