package Domain.Repositories;

import Domain.Entities.RecoveryCode;

import java.util.Optional;
import java.util.UUID;

public interface RecoveryCodeRepository extends Repository<RecoveryCode> {

    Optional<RecoveryCode> findByUserIdAndStatus(UUID userId, String status);
}
