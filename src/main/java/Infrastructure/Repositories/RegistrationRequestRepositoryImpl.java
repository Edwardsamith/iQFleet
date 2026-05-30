package Infrastructure.Repositories;

import Domain.Entities.RegistrationRequest;
import Domain.Enums.DecisionStatus;
import Domain.Repositories.RegistrationRequestRepository;
import Infrastructure.Persistence.Entities.UserJpaEntity;
import Infrastructure.Persistence.Mappers.RegistrationRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RegistrationRequestRepositoryImpl implements RegistrationRequestRepository {

    private final JpaRegistrationRequestRepository jpaRepo;
    private final JpaUserRepository                jpaUserRepo;

    @Override
    public RegistrationRequest save(RegistrationRequest domain) {
        UserJpaEntity userRef       = jpaUserRepo.getReferenceById(domain.getUserId());
        UserJpaEntity reviewedByRef = domain.getReviewedById() != null ? jpaUserRepo.getReferenceById(domain.getReviewedById()) : null;
        return RegistrationRequestMapper.toDomain(jpaRepo.save(RegistrationRequestMapper.toJpa(domain, userRef, reviewedByRef)));
    }

    @Override
    public Optional<RegistrationRequest> findById(UUID id) {
        return jpaRepo.findById(id).map(RegistrationRequestMapper::toDomain);
    }

    @Override
    public List<RegistrationRequest> findAll() {
        return jpaRepo.findAll().stream().map(RegistrationRequestMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepo.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepo.deleteById(id);
    }

    @Override
    public Optional<RegistrationRequest> findByUserId(UUID userId) {
        return jpaRepo.findByUser_Id(userId).map(RegistrationRequestMapper::toDomain);
    }

    @Override
    public List<RegistrationRequest> findByDecisionIsNull() {
        return jpaRepo.findByDecisionIsNull().stream().map(RegistrationRequestMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<RegistrationRequest> findByDecision(DecisionStatus decision) {
        return jpaRepo.findByDecision(decision).stream().map(RegistrationRequestMapper::toDomain).collect(Collectors.toList());
    }
}
