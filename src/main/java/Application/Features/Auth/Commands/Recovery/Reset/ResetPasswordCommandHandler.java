package Application.Features.Auth.Commands.Recovery.Reset;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.UserRepository;
import Infrastructure.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordCommandHandler
        implements IRequestHandler<ResetPasswordCommand, ResetPasswordResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Result<ResetPasswordResponse> handle(ResetPasswordCommand command) {
        if (command.resetToken() == null || command.resetToken().isBlank()) {
            return Result.Failure("Token de recuperación requerido");
        }
        if (command.newPassword() == null || command.newPassword().isBlank()) {
            return Result.Failure("La nueva contraseña es obligatoria");
        }

        if (!jwtService.isTokenValid(command.resetToken())) {
            return Result.Failure("El enlace de recuperación es inválido o ha expirado");
        }

        if (!jwtService.isRecoveryToken(command.resetToken())) {
            return Result.Failure("Token inválido");
        }

        String password = command.newPassword();
        if (password.length() < 8) {
            return Result.Failure("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            return Result.Failure("La contraseña debe contener al menos una letra mayúscula");
        }
        if (!password.matches(".*[0-9].*")) {
            return Result.Failure("La contraseña debe contener al menos un número");
        }

        String email = jwtService.extractEmail(command.resetToken());
        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return Result.Failure("Usuario no encontrado");
        }

        var user = optionalUser.get();
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);

        return Result.Success(new ResetPasswordResponse(
                "Contraseña actualizada correctamente. Ya puedes iniciar sesión."
        ));
    }
}
