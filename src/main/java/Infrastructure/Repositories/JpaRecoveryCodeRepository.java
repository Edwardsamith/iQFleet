package Infrastructure.Repositories;

import Domain.Entities.RecoveryCode;
import Domain.Repositories.RecoveryCodeRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRecoveryCodeRepository
        extends GenericJpaRepository<RecoveryCode>, RecoveryCodeRepository {

    Optional<RecoveryCode> findByUserIdAndStatus(UUID userId, String status);
}
