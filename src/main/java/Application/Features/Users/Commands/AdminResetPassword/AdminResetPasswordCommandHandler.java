package Application.Features.Users.Commands.AdminResetPassword;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminResetPasswordCommandHandler
        implements IRequestHandler<AdminResetPasswordCommand, String> {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<String> handle(AdminResetPasswordCommand command) {
        if (command.newPassword() == null || command.newPassword().isBlank()) {
            return Result.Failure("La nueva contraseña es obligatoria");
        }

        String pwd = command.newPassword();
        if (pwd.length() < 8) return Result.Failure("La contraseña debe tener al menos 8 caracteres");
        if (!pwd.matches(".*[A-Z].*")) return Result.Failure("La contraseña debe contener al menos una mayúscula");
        if (!pwd.matches(".*[0-9].*")) return Result.Failure("La contraseña debe contener al menos un número");

        var optional = repository.findById(command.userId());
        if (optional.isEmpty()) {
            return Result.Failure("Usuario no encontrado");
        }

        var user = optional.get();
        user.setPasswordHash(passwordEncoder.encode(pwd));
        repository.save(user);

        return Result.Success("Contraseña restablecida correctamente");
    }
}
