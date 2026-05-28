package Domain.Repositories;

import Domain.Entities.Example;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExampleRepository {

    Example save(Example example);

    Optional<Example> findById(UUID id);

    List<Example> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
