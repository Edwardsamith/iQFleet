package Application.Features.Users.Queries.GetById;

import Application.Abstractions.IRequestHandler;
import Application.Features.Users.Queries.UserDetailResponse;
import Application.Result.Result;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserByIdQueryHandler
        implements IRequestHandler<GetUserByIdQuery, UserDetailResponse> {

    private final UserRepository repository;

    @Override
    public Result<UserDetailResponse> handle(GetUserByIdQuery query) {
        var optional = repository.findById(query.userId());
        if (optional.isEmpty()) {
            return Result.Failure("Usuario no encontrado");
        }

        var user = optional.get();
        return Result.Success(new UserDetailResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getIdentificationType() != null ? user.getIdentificationType().name() : null,
                user.getIdentificationNumber(),
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getStatus() != null ? user.getStatus().name() : null,
                user.getLastAccess() != null ? user.getLastAccess().toString() : null,
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null
        ));
    }
}
