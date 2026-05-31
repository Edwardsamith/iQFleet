package Application.Features.Users.Commands.ChangeStatus;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangeUserStatusCommandHandler
        implements IRequestHandler<ChangeUserStatusCommand, String> {

    private final UserRepository repository;

    @Override
    public Result<String> handle(ChangeUserStatusCommand command) {
        var optional = repository.findById(command.userId());
        if (optional.isEmpty()) {
            return Result.Failure("Usuario no encontrado");
        }

        var user = optional.get();
        user.setStatus(command.newStatus());
        repository.save(user);

        return Result.Success("Estado actualizado a " + command.newStatus().name());
    }
}
