package Infrastructure.Repositories;

import Domain.Entities.Entity;
import Domain.Repositories.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface GenericJpaRepository<T extends Entity>
        extends JpaRepository<T, UUID>, Repository<T> {

    // save(T), findById(UUID), findAll(), existsById(UUID) are already
    // provided by JpaRepository with the same signatures — no override needed.

    default T saveee(T entity) {
        return save(entity);
    }

    default T update(T entity) {
        return save(entity);
    }

    default void delete(UUID id) {
        deleteById(id);
    }
}
