package Infrastructure.Repositories;

import Domain.Entities.RegistrationRequest;
import Domain.Enums.DecisionStatus;
import Domain.Repositories.RegistrationRequestRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRegistrationRequestRepository
        extends GenericJpaRepository<RegistrationRequest>, RegistrationRequestRepository {

    Optional<RegistrationRequest> findByUserId(UUID userId);

    List<RegistrationRequest> findByDecisionIsNull();

    List<RegistrationRequest> findByDecision(DecisionStatus decision);
}
