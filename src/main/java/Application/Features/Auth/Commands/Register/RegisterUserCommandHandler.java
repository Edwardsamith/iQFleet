package Application.Features.Auth.Commands.Register;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.RegistrationRequest;
import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.RegistrationRequestRepository;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class RegisterUserCommandHandler
        implements IRequestHandler<RegisterUserCommand, RegisterUserResponse> {

    private final UserRepository repository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<RegisterUserResponse> handle(RegisterUserCommand command) {

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

        if (command.identificationNumber() != null && !command.identificationNumber().isBlank()
                && command.identificationType() != null
                && repository.existsByIdentificationTypeAndIdentificationNumber(
                        command.identificationType().name(), command.identificationNumber())) {
            errors.add("Ya existe un usuario registrado con ese número de identificación");
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
                    .passwordHash(passwordEncoder.encode(command.password()))
                    .role(command.role())
                    .status(UserStatus.PENDING)
                    .build();

            User savedUser = repository.save(user);

            RegistrationRequest solicitud = RegistrationRequest.builder()
                    .userId(savedUser.getId())
                    .build();
            registrationRequestRepository.save(solicitud);

            return Result.Success(
                    new RegisterUserResponse(
                            savedUser.getId(),
                            savedUser.getUsername(),
                            "Solicitud de registro enviada. Pendiente de aprobación."
                    )
            );

        } catch (Exception e) {
            return Result.Failure("Error al registrar usuario: " + e.getMessage());
        }
    }
}
