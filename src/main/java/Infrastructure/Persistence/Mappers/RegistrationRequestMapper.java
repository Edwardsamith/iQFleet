package Infrastructure.Persistence.Mappers;

import Domain.Entities.RegistrationRequest;
import Infrastructure.Persistence.Entities.RegistrationRequestJpaEntity;
import Infrastructure.Persistence.Entities.UserJpaEntity;

public class RegistrationRequestMapper {

    public static RegistrationRequest toDomain(RegistrationRequestJpaEntity jpa) {
        RegistrationRequest domain = new RegistrationRequest();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setUserId(jpa.getUser().getId());
        domain.setDecision(jpa.getDecision());
        domain.setRejectionReason(jpa.getRejectionReason());
        if (jpa.getReviewedBy() != null) domain.setReviewedById(jpa.getReviewedBy().getId());
        domain.setDecisionDate(jpa.getDecisionDate());
        return domain;
    }

    public static RegistrationRequestJpaEntity toJpa(RegistrationRequest domain,
                                                      UserJpaEntity userRef,
                                                      UserJpaEntity reviewedByRef) {
        RegistrationRequestJpaEntity jpa = new RegistrationRequestJpaEntity();
        jpa.setId(domain.getId());
        jpa.setUser(userRef);
        jpa.setDecision(domain.getDecision());
        jpa.setRejectionReason(domain.getRejectionReason());
        jpa.setReviewedBy(reviewedByRef);
        jpa.setDecisionDate(domain.getDecisionDate());
        return jpa;
    }
}
