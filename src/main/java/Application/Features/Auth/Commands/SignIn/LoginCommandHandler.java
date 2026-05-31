package Application.Features.Auth.Commands.SignIn;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.UserRepository;
import Infrastructure.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginCommandHandler implements IRequestHandler<LoginCommand, LoginResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    public Result<LoginResponse> handle(LoginCommand command) {

        if (command.email() == null || command.email().isBlank()) {
            return Result.Failure("El correo electrónico es obligatorio");
        }

        if (command.password() == null || command.password().isBlank()) {
            return Result.Failure("La contraseña es obligatoria");
        }

        var optionalUser = userRepository.findByEmail(command.email());

        if (optionalUser.isEmpty()) {
            return Result.Failure("Correo electrónico o contraseña incorrectos");
        }

        User user = optionalUser.get();

        if (user.getStatus() == UserStatus.BLOCKED) {
            if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
                return Result.Failure("Cuenta bloqueada temporalmente. Intente de nuevo más tarde");
            }
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            user.setStatus(UserStatus.ACTIVE);
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            return Result.Failure("La cuenta se encuentra inactiva");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            return Result.Failure("La cuenta está pendiente de aprobación");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            return Result.Failure("La cuenta no se encuentra activa");
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            int newAttempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(newAttempts);

            if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setStatus(UserStatus.BLOCKED);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                userRepository.save(user);
                return Result.Failure("Cuenta bloqueada por " + MAX_FAILED_ATTEMPTS + " intentos fallidos. Intente de nuevo en " + LOCK_DURATION_MINUTES + " minutos");
            }

            userRepository.save(user);
            return Result.Failure("Correo electrónico o contraseña incorrectos");
        }

        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        user.setLastAccess(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        return Result.Success(new LoginResponse(
                token,
                user.getRole().name(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        ));
    }
}
