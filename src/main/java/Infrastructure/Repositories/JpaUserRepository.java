package Infrastructure.Repositories;

import Domain.Enums.UserStatus;
import Infrastructure.Persistence.Entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByIdentificationTypeAndIdentificationNumber(String identificationType, String identificationNumber);

    List<UserJpaEntity> findByStatus(UserStatus status);
}
