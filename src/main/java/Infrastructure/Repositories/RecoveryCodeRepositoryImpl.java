package Infrastructure.Repositories;

import Domain.Entities.RecoveryCode;
import Domain.Repositories.RecoveryCodeRepository;
import Infrastructure.Persistence.Entities.UserJpaEntity;
import Infrastructure.Persistence.Mappers.RecoveryCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecoveryCodeRepositoryImpl implements RecoveryCodeRepository {

    private final JpaRecoveryCodeRepository jpaRepo;
    private final JpaUserRepository         jpaUserRepo;

    @Override
    public RecoveryCode save(RecoveryCode domain) {
        UserJpaEntity userRef = jpaUserRepo.getReferenceById(domain.getUserId());
        return RecoveryCodeMapper.toDomain(jpaRepo.save(RecoveryCodeMapper.toJpa(domain, userRef)));
    }

    @Override
    public Optional<RecoveryCode> findById(UUID id) {
        return jpaRepo.findById(id).map(RecoveryCodeMapper::toDomain);
    }

    @Override
    public List<RecoveryCode> findAll() {
        return jpaRepo.findAll().stream().map(RecoveryCodeMapper::toDomain).collect(Collectors.toList());
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
    public Optional<RecoveryCode> findByUserIdAndStatus(UUID userId, String status) {
        return jpaRepo.findByUser_IdAndStatus(userId, status).map(RecoveryCodeMapper::toDomain);
    }
}
