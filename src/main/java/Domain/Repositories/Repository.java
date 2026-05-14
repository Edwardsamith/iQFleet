package Domain.Repositories;

import Domain.Entities.Entity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T extends Entity> {

    void save(T entity) throws Exception;

    void update(T entity) throws Exception;

    void delete(UUID id) throws Exception;

    Optional<T> findById(UUID id) throws Exception;

    List<T> findAll() throws Exception;

    boolean existsById(UUID id) throws Exception;
}