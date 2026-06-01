package Application.Features.Auth.Commands.Recovery.Verify;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.RecoveryCodeRepository;
import Domain.Repositories.UserRepository;
import Infrastructure.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VerifyRecoveryCodeCommandHandler
        implements IRequestHandler<VerifyRecoveryCodeCommand, VerifyRecoveryCodeResponse> {

    private final UserRepository userRepository;
    private final RecoveryCodeRepository recoveryCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Result<VerifyRecoveryCodeResponse> handle(VerifyRecoveryCodeCommand command) {
        if (command.email() == null || command.email().isBlank()) {
            return Result.Failure("El correo electrónico es obligatorio");
        }
        if (command.code() == null || command.code().isBlank()) {
            return Result.Failure("El código es obligatorio");
        }

        var optionalUser = userRepository.findByEmail(command.email().trim().toLowerCase());
        if (optionalUser.isEmpty()) {
            return Result.Failure("Código inválido o expirado");
        }

        var user = optionalUser.get();
        var optionalCode = recoveryCodeRepository.findByUserIdAndStatus(user.getId(), "PENDING");

        if (optionalCode.isEmpty()) {
            return Result.Failure("Código inválido o expirado");
        }

        var recoveryCode = optionalCode.get();

        if (LocalDateTime.now().isAfter(recoveryCode.getExpiresAt())) {
            recoveryCode.setStatus("EXPIRED");
            recoveryCodeRepository.save(recoveryCode);
            return Result.Failure("El código ha expirado. Solicita uno nuevo.");
        }

        if (!passwordEncoder.matches(command.code().trim(), recoveryCode.getCodeHash())) {
            return Result.Failure("Código inválido o expirado");
        }

        recoveryCode.setStatus("USED");
        recoveryCode.setUsedAt(LocalDateTime.now());
        recoveryCodeRepository.save(recoveryCode);

        String resetToken = jwtService.generateRecoveryToken(user.getEmail());
        return Result.Success(new VerifyRecoveryCodeResponse(resetToken));
    }
}
