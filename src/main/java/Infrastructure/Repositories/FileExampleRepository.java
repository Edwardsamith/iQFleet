package Infrastructure.Repositories;
import Domain.Entities.Example;
import Domain.Repositories.ExampleRepository;

public class FileExampleRepository extends FileRepository<Example> implements ExampleRepository {

    public FileExampleRepository()
    { super("Example", null); }


}