package Application.Features.Users.Queries.GetAll;

import Application.Abstractions.IQuery;
import java.util.List;
import Domain.Entities.User;

public record GetAllUsersQuery() implements IQuery<List<User>>{
}
