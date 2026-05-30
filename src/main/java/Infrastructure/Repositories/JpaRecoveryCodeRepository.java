package Infrastructure.Repositories;

import Infrastructure.Persistence.Entities.RecoveryCodeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRecoveryCodeRepository extends JpaRepository<RecoveryCodeJpaEntity, UUID> {

    Optional<RecoveryCodeJpaEntity> findByUser_IdAndStatus(UUID userId, String status);
}
