package Infrastructure.Repositories;
import Domain.Entities.Example;
import Domain.Repositories.ExampleRepository;
import Infrastructure.Persistence.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class HibernateExampleRepository extends HibernateRepository<Example> implements ExampleRepository {

    protected HibernateExampleRepository(Class<Example> example) {
        super(example);
    }


}