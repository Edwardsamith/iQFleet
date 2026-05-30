package Application.Features.Users.Disable;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;

public class DisableUserCommandHandler
        implements IRequestHandler<
        DisableUserCommand,
        String> {

    private final UserRepository repository;

    public DisableUserCommandHandler(
            UserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Result<String> handle(
            DisableUserCommand command
    ) {

        var optionalUser = repository.findById(
                command.userId()
        );

        if (optionalUser.isEmpty()) {

            return Result.Failure(
                    "Usuario no encontrado"
            );
        }

        var user = optionalUser.get();

        user.setStatus(UserStatus.INACTIVE);

        repository.save(user);

        return Result.Success(
                "Usuario desactivado correctamente"
        );
    }
}