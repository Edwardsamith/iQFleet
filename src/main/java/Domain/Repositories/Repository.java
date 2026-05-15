package Domain.Repositories;

import Domain.Entities.Entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T extends Entity> {

    T saveee(T entity);

    T update(T entity);

    void delete(UUID id);

    Optional<T> findById(UUID id);

    List<T> findAll();

    boolean existsById(UUID id);
}
