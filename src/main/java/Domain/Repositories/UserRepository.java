package Domain.Repositories;

import Domain.Entities.User;
import Domain.Enums.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByIdentificationTypeAndIdentificationNumber(String identificationType, String identificationNumber);

    List<User> findByStatus(UserStatus status);
}
