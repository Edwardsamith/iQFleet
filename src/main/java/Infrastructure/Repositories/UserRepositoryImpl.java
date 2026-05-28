package Infrastructure.Repositories;

import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;
import Infrastructure.Persistence.Entities.UserJpaEntity;
import Infrastructure.Persistence.Mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepo;

    @Override
    public User save(User domain) {
        return UserMapper.toDomain(jpaRepo.save(UserMapper.toJpa(domain)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepo.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepo.findAll().stream().map(UserMapper::toDomain).collect(Collectors.toList());
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
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepo.findByUsername(username).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepo.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepo.existsByUsername(username);
    }

    @Override
    public boolean existsByIdentificationTypeAndIdentificationNumber(String identificationType, String identificationNumber) {
        return jpaRepo.existsByIdentificationTypeAndIdentificationNumber(identificationType, identificationNumber);
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return jpaRepo.findByStatus(status).stream().map(UserMapper::toDomain).collect(Collectors.toList());
    }
}
