package Application.Features.Users.Queries.GetByFilter;

import Application.Abstractions.IRequestHandler;
import Application.Features.Users.Queries.UserDetailResponse;
import Application.Result.Result;
import Domain.Entities.User;
import Domain.Enums.Role;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUsersByFilterQueryHandler
        implements IRequestHandler<GetUsersByFilterQuery, List<UserDetailResponse>> {

    private final UserRepository repository;

    @Override
    public Result<List<UserDetailResponse>> handle(GetUsersByFilterQuery query) {
        List<User> users = repository.findAll();

        if (query.status() != null && !query.status().isBlank()) {
            try {
                UserStatus status = UserStatus.valueOf(query.status());
                users = users.stream().filter(u -> u.getStatus() == status).toList();
            } catch (IllegalArgumentException e) {
                return Result.Failure("Estado inválido: " + query.status());
            }
        }

        if (query.role() != null && !query.role().isBlank()) {
            try {
                Role role = Role.valueOf(query.role());
                users = users.stream().filter(u -> u.getRole() == role).toList();
            } catch (IllegalArgumentException e) {
                return Result.Failure("Rol inválido: " + query.role());
            }
        }

        List<UserDetailResponse> result = users.stream().map(u -> new UserDetailResponse(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getIdentificationType() != null ? u.getIdentificationType().name() : null,
                u.getIdentificationNumber(),
                u.getEmail(),
                u.getPhone(),
                u.getUsername(),
                u.getRole() != null ? u.getRole().name() : null,
                u.getStatus() != null ? u.getStatus().name() : null,
                u.getLastAccess() != null ? u.getLastAccess().toString() : null,
                u.getCreatedAt() != null ? u.getCreatedAt().toString() : null
        )).toList();

        return Result.Success(result);
    }
}
