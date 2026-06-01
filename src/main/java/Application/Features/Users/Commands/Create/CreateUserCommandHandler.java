package Application.Features.Users.Commands.Create;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CreateUserCommandHandler
        implements IRequestHandler<CreateUserCommand, CreateUserResponse> {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<CreateUserResponse> handle(CreateUserCommand command) {
        ArrayList<String> errors = new ArrayList<>();

        if (command.firstName() == null || command.firstName().isBlank())
            errors.add("El nombre es obligatorio");
        if (command.lastName() == null || command.lastName().isBlank())
            errors.add("El apellido es obligatorio");
        if (command.email() == null || command.email().isBlank())
            errors.add("El correo es obligatorio");
        if (command.username() == null || command.username().isBlank())
            errors.add("El nombre de usuario es obligatorio");
        if (command.password() == null || command.password().isBlank())
            errors.add("La contraseña es obligatoria");
        if (command.role() == null)
            errors.add("El rol es obligatorio");

        if (command.email() != null && repository.existsByEmail(command.email()))
            errors.add("El correo ya está registrado");
        if (command.username() != null && repository.existsByUsername(command.username()))
            errors.add("El nombre de usuario ya está registrado");
        if (command.identificationType() != null && command.identificationNumber() != null
                && !command.identificationNumber().isBlank()
                && repository.existsByIdentificationTypeAndIdentificationNumber(
                        command.identificationType().name(), command.identificationNumber()))
            errors.add("Ya existe un usuario con ese número de identificación");

        if (command.password() != null) {
            String pwd = command.password();
            if (pwd.length() < 8) errors.add("La contraseña debe tener al menos 8 caracteres");
            if (!pwd.matches(".*[A-Z].*")) errors.add("La contraseña debe contener al menos una mayúscula");
            if (!pwd.matches(".*[0-9].*")) errors.add("La contraseña debe contener al menos un número");
        }

        if (!errors.isEmpty()) return Result.Failure(errors);

        try {
            UserStatus status = command.status() != null ? command.status() : UserStatus.ACTIVE;
            User user = User.builder()
                    .firstName(command.firstName())
                    .lastName(command.lastName())
                    .identificationType(command.identificationType())
                    .identificationNumber(command.identificationNumber())
                    .email(command.email())
                    .phone(command.phone())
                    .username(command.username())
                    .passwordHash(passwordEncoder.encode(command.password()))
                    .role(command.role())
                    .status(status)
                    .build();

            User saved = repository.save(user);
            return Result.Success(new CreateUserResponse(saved.getId(), saved.getUsername(),
                    "Usuario creado correctamente"));
        } catch (Exception e) {
            return Result.Failure("Error al crear usuario: " + e.getMessage());
        }
    }
}
