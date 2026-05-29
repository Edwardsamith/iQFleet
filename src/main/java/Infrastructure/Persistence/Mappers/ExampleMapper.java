package Infrastructure.Persistence.Mappers;

import Domain.Entities.Example;
import Infrastructure.Persistence.Entities.ExampleJpaEntity;

public class ExampleMapper {

    public static Example toDomain(ExampleJpaEntity jpa) {
        Example domain = new Example();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setName(jpa.getName());
        domain.setApellido(jpa.getApellido());
        return domain;
    }

    public static ExampleJpaEntity toJpa(Example domain) {
        ExampleJpaEntity jpa = new ExampleJpaEntity();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setApellido(domain.getApellido());
        return jpa;
    }
}
