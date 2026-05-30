package Infrastructure.Persistence.Mappers;

import Domain.Entities.User;
import Infrastructure.Persistence.Entities.UserJpaEntity;

public class UserMapper {

    public static User toDomain(UserJpaEntity jpa) {
        User domain = new User();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setFirstName(jpa.getFirstName());
        domain.setLastName(jpa.getLastName());
        domain.setIdentificationType(jpa.getIdentificationType());
        domain.setIdentificationNumber(jpa.getIdentificationNumber());
        domain.setEmail(jpa.getEmail());
        domain.setPhone(jpa.getPhone());
        domain.setUsername(jpa.getUsername());
        domain.setPasswordHash(jpa.getPasswordHash());
        domain.setRole(jpa.getRole());
        domain.setStatus(jpa.getStatus());
        domain.setFailedAttempts(jpa.getFailedAttempts());
        domain.setLockedUntil(jpa.getLockedUntil());
        domain.setLastAccess(jpa.getLastAccess());
        return domain;
    }

    public static UserJpaEntity toJpa(User domain) {
        UserJpaEntity jpa = new UserJpaEntity();
        jpa.setId(domain.getId());
        jpa.setFirstName(domain.getFirstName());
        jpa.setLastName(domain.getLastName());
        jpa.setIdentificationType(domain.getIdentificationType());
        jpa.setIdentificationNumber(domain.getIdentificationNumber());
        jpa.setEmail(domain.getEmail());
        jpa.setPhone(domain.getPhone());
        jpa.setUsername(domain.getUsername());
        jpa.setPasswordHash(domain.getPasswordHash());
        jpa.setRole(domain.getRole());
        jpa.setStatus(domain.getStatus());
        jpa.setFailedAttempts(domain.getFailedAttempts());
        jpa.setLockedUntil(domain.getLockedUntil());
        jpa.setLastAccess(domain.getLastAccess());
        return jpa;
    }
}
