package Domain.Repositories;

import Domain.Entities.RecoveryCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecoveryCodeRepository {

    RecoveryCode save(RecoveryCode recoveryCode);

    Optional<RecoveryCode> findById(UUID id);

    List<RecoveryCode> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<RecoveryCode> findByUserIdAndStatus(UUID userId, String status);
}
