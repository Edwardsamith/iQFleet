package Domain.Repositories;

import Domain.Entities.RegistrationRequest;
import Domain.Enums.DecisionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRequestRepository {

    RegistrationRequest save(RegistrationRequest registrationRequest);

    Optional<RegistrationRequest> findById(UUID id);

    List<RegistrationRequest> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<RegistrationRequest> findByUserId(UUID userId);

    List<RegistrationRequest> findByDecisionIsNull();

    List<RegistrationRequest> findByDecision(DecisionStatus decision);
}
