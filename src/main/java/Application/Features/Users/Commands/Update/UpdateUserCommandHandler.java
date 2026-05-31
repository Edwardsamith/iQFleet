package Application.Features.Users.Commands.Update;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.UserRepository;

public class UpdateUserCommandHandler implements IRequestHandler<UpdateUserCommand, UpdateUserResponse> {
    private final UserRepository repository;

    public UpdateUserCommandHandler(UserRepository repository){
        this.repository = repository;
    }

    @Override
    public Result<UpdateUserResponse> handle(UpdateUserCommand command){
        var optionalUser = repository.findById(command.userId());

        if(optionalUser.isEmpty()){
            return Result.Failure("Usuario no encontrado");
        }
        var user = optionalUser.get();

        user.setFirstName(
                command.firstName()
        );

        user.setLastName(
                command.lastName()
        );

        user.setPhone(
                command.phone()
        );

        user.setEmail(
                command.email()
        );

        user.setRole(
                command.role()
        );

        repository.save(user);

        return Result.Success(
                new UpdateUserResponse(
                        user.getId(),
                        user.getUsername(),
                        "Usuario actualizado correctamente"
                )
        );
    }
    }


