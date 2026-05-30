package Infrastructure.Repositories;

import Domain.Enums.DecisionStatus;
import Infrastructure.Persistence.Entities.RegistrationRequestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRegistrationRequestRepository extends JpaRepository<RegistrationRequestJpaEntity, UUID> {

    Optional<RegistrationRequestJpaEntity> findByUser_Id(UUID userId);

    List<RegistrationRequestJpaEntity> findByDecisionIsNull();

    List<RegistrationRequestJpaEntity> findByDecision(DecisionStatus decision);
}
