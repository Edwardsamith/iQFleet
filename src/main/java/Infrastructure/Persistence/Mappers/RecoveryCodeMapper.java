package Infrastructure.Persistence.Mappers;

import Domain.Entities.RecoveryCode;
import Infrastructure.Persistence.Entities.RecoveryCodeJpaEntity;
import Infrastructure.Persistence.Entities.UserJpaEntity;

public class RecoveryCodeMapper {

    public static RecoveryCode toDomain(RecoveryCodeJpaEntity jpa) {
        RecoveryCode domain = new RecoveryCode();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setUserId(jpa.getUser().getId());
        domain.setCodeHash(jpa.getCodeHash());
        domain.setDeliveryMethod(jpa.getDeliveryMethod());
        domain.setRecipient(jpa.getRecipient());
        domain.setStatus(jpa.getStatus());
        domain.setExpiresAt(jpa.getExpiresAt());
        domain.setUsedAt(jpa.getUsedAt());
        return domain;
    }

    public static RecoveryCodeJpaEntity toJpa(RecoveryCode domain, UserJpaEntity userRef) {
        RecoveryCodeJpaEntity jpa = new RecoveryCodeJpaEntity();
        jpa.setId(domain.getId());
        jpa.setUser(userRef);
        jpa.setCodeHash(domain.getCodeHash());
        jpa.setDeliveryMethod(domain.getDeliveryMethod());
        jpa.setRecipient(domain.getRecipient());
        jpa.setStatus(domain.getStatus());
        jpa.setExpiresAt(domain.getExpiresAt());
        jpa.setUsedAt(domain.getUsedAt());
        return jpa;
    }
}
