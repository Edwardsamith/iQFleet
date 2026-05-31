package Application.Features.Auth.Register;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;

import java.util.ArrayList;

public class RegisterUserCommandHandler
        implements IRequestHandler<
        RegisterUserCommand,
        RegisterUserResponse> {

    private final UserRepository repository;

    public RegisterUserCommandHandler(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Result<RegisterUserResponse> handle(
            RegisterUserCommand command) {

        ArrayList<String> errors = new ArrayList<>();

        if (command.firstName() == null || command.firstName().isBlank()) {
            errors.add("El nombre es obligatorio");
        }

        if (command.lastName() == null || command.lastName().isBlank()) {
            errors.add("El apellido es obligatorio");
        }

        if (command.email() == null || command.email().isBlank()) {
            errors.add("El correo es obligatorio");
        }

        if (command.username() == null || command.username().isBlank()) {
            errors.add("El username es obligatorio");
        }

        if (command.password() == null || command.password().isBlank()) {
            errors.add("La contraseña es obligatoria");
        }

        if (repository.existsByEmail(command.email())) {
            errors.add("El correo ya existe");
        }

        if (repository.existsByUsername(command.username())) {
            errors.add("El username ya existe");
        }

        if (!errors.isEmpty()) {
            return Result.Failure(errors);
        }

        try {

            User user = User.builder()
                    .firstName(command.firstName())
                    .lastName(command.lastName())
                    .identificationType(command.identificationType())
                    .identificationNumber(command.identificationNumber())
                    .email(command.email())
                    .phone(command.phone())
                    .username(command.username())
                    .passwordHash(command.password())
                    .role(command.role())
                    .status(UserStatus.ACTIVE)
                    .build();

            repository.save(user);

            return Result.Success(
                    new RegisterUserResponse(
                            user.getId(),
                            user.getUsername(),
                            "Usuario registrado correctamente"
                    )
            );

        } catch (Exception e) {

            return Result.Failure(
                    "Error al registrar usuario: "
                            + e.getMessage()
            );
        }
    }
}