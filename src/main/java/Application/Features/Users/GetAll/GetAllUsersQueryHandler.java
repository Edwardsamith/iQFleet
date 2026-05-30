package Application.Features.Users.GetAll;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.User;
import Domain.Repositories.UserRepository;

import java.util.List;

public class GetAllUsersQueryHandler
        implements IRequestHandler<
        GetAllUsersQuery,
        List<User>> {

    private final UserRepository repository;

    public GetAllUsersQueryHandler(
            UserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Result<List<User>> handle(
            GetAllUsersQuery query
    ) {

        return Result.Success(
                repository.findAll()
        );
    }
}

