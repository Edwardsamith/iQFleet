package Infrastructure.Repositories;

import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends GenericJpaRepository<User>, UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByIdentificationTypeAndIdentificationNumber(String identificationType, String identificationNumber);

    List<User> findByStatus(UserStatus status);
}
