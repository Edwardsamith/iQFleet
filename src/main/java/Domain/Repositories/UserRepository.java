package Domain.Repositories;

import Domain.Entities.User;
import Domain.Enums.UserStatus;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByIdentificationTypeAndIdentificationNumber(String identificationType, String identificationNumber);

    List<User> findByStatus(UserStatus status);
}
