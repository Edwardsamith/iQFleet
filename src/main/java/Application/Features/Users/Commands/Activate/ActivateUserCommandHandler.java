package Application.Features.Users.Commands.Activate;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;

public class ActivateUserCommandHandler
        implements IRequestHandler<
        ActivateUserCommand,
        String> {

    private final UserRepository repository;

    public ActivateUserCommandHandler(
            UserRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Result<String> handle(
            ActivateUserCommand command
    ) {

        var optionalUser =
                repository.findById(
                        command.userId()
                );

        if (optionalUser.isEmpty()) {

            return Result.Failure(
                    "Usuario no encontrado"
            );
        }

        var user = optionalUser.get();

        user.setStatus(UserStatus.ACTIVE);

        repository.save(user);

        return Result.Success(
                "Usuario activado correctamente"
        );
    }
}