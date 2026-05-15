package Domain.Repositories;

import Domain.Entities.RegistrationRequest;
import Domain.Enums.DecisionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRequestRepository extends Repository<RegistrationRequest> {

    Optional<RegistrationRequest> findByUserId(UUID userId);

    List<RegistrationRequest> findByDecisionIsNull();

    List<RegistrationRequest> findByDecision(DecisionStatus decision);
}
