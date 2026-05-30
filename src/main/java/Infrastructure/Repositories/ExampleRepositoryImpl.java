package Infrastructure.Repositories;

import Domain.Entities.Example;
import Domain.Repositories.ExampleRepository;
import Infrastructure.Persistence.Entities.ExampleJpaEntity;
import Infrastructure.Persistence.Mappers.ExampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ExampleRepositoryImpl implements ExampleRepository {

    private final JpaExampleRepository jpaRepo;

    @Override
    public Example save(Example domain) {
        ExampleJpaEntity saved = jpaRepo.save(ExampleMapper.toJpa(domain));
        return ExampleMapper.toDomain(saved);
    }

    @Override
    public Optional<Example> findById(UUID id) {
        return jpaRepo.findById(id).map(ExampleMapper::toDomain);
    }

    @Override
    public List<Example> findAll() {
        return jpaRepo.findAll().stream().map(ExampleMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepo.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepo.deleteById(id);
    }
}
