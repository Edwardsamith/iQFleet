package Infrastructure.Persistence.Mappers;

import Domain.Entities.Driver;
import Infrastructure.Persistence.Entities.DriverJpaEntity;

public class DriverMapper {

    public static Driver toDomain(DriverJpaEntity jpa) {
        Driver domain = new Driver();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setIdentificationType(jpa.getIdentificationType());
        domain.setIdentificationNumber(jpa.getIdentificationNumber());
        domain.setFirstName(jpa.getFirstName());
        domain.setLastName(jpa.getLastName());
        domain.setLicenseNumber(jpa.getLicenseNumber());
        domain.setLicenseCategory(jpa.getLicenseCategory());
        domain.setLicenseExpiry(jpa.getLicenseExpiry());
        domain.setPhone(jpa.getPhone());
        domain.setEmail(jpa.getEmail());
        domain.setAddress(jpa.getAddress());
        domain.setStatus(jpa.getStatus());
        domain.setRegistrationDate(jpa.getRegistrationDate());
        domain.setDeactivationDate(jpa.getDeactivationDate());
        domain.setDeactivationReason(jpa.getDeactivationReason());
        return domain;
    }

    public static DriverJpaEntity toJpa(Driver domain) {
        DriverJpaEntity jpa = new DriverJpaEntity();
        jpa.setId(domain.getId());
        jpa.setIdentificationType(domain.getIdentificationType());
        jpa.setIdentificationNumber(domain.getIdentificationNumber());
        jpa.setFirstName(domain.getFirstName());
        jpa.setLastName(domain.getLastName());
        jpa.setLicenseNumber(domain.getLicenseNumber());
        jpa.setLicenseCategory(domain.getLicenseCategory());
        jpa.setLicenseExpiry(domain.getLicenseExpiry());
        jpa.setPhone(domain.getPhone());
        jpa.setEmail(domain.getEmail());
        jpa.setAddress(domain.getAddress());
        jpa.setStatus(domain.getStatus());
        jpa.setRegistrationDate(domain.getRegistrationDate());
        jpa.setDeactivationDate(domain.getDeactivationDate());
        jpa.setDeactivationReason(domain.getDeactivationReason());
        return jpa;
    }
}
