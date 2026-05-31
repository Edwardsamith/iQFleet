package Application.Features.Users.Commands.Update;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateUserCommandHandler implements IRequestHandler<UpdateUserCommand, UpdateUserResponse> {

    private final UserRepository repository;

    @Override
    public Result<UpdateUserResponse> handle(UpdateUserCommand command) {
        var optionalUser = repository.findById(command.userId());
        if (optionalUser.isEmpty()) {
            return Result.Failure("Usuario no encontrado");
        }

        var user = optionalUser.get();
        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.lastName() != null) user.setLastName(command.lastName());
        if (command.phone() != null) user.setPhone(command.phone());
        if (command.email() != null) user.setEmail(command.email());
        if (command.role() != null) user.setRole(command.role());

        repository.save(user);

        return Result.Success(new UpdateUserResponse(
                user.getId(),
                user.getUsername(),
                "Usuario actualizado correctamente"
        ));
    }
}
