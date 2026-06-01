package Application.Features.Auth.Commands.Recovery.Request;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.RecoveryCode;
import Domain.Enums.RecoveryMethod;
import Domain.Repositories.RecoveryCodeRepository;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecoverPasswordCommandHandler
        implements IRequestHandler<RecoverPasswordCommand, RecoverPasswordResponse> {

    private final UserRepository userRepository;
    private final RecoveryCodeRepository recoveryCodeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String GENERIC_MESSAGE =
            "Si el correo está registrado, recibirás un código de verificación.";

    @Override
    public Result<RecoverPasswordResponse> handle(RecoverPasswordCommand command) {
        if (command.recipient() == null || command.recipient().isBlank()) {
            return Result.Failure("El correo electrónico es obligatorio");
        }

        var optionalUser = userRepository.findByEmail(command.recipient().trim().toLowerCase());

        if (optionalUser.isEmpty()) {
            return Result.Success(new RecoverPasswordResponse(GENERIC_MESSAGE));
        }

        var user = optionalUser.get();

        recoveryCodeRepository.findByUserIdAndStatus(user.getId(), "PENDING").ifPresent(existing -> {
            existing.setStatus("EXPIRED");
            recoveryCodeRepository.save(existing);
        });

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        String otpHash = passwordEncoder.encode(otp);

        RecoveryCode code = RecoveryCode.builder()
                .userId(user.getId())
                .codeHash(otpHash)
                .deliveryMethod(RecoveryMethod.EMAIL)
                .recipient(user.getEmail())
                .status("PENDING")
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        recoveryCodeRepository.save(code);

        log.info("=== RECOVERY OTP for {} === Code: {} (valid 15 min) ===", user.getEmail(), otp);

        return Result.Success(new RecoverPasswordResponse(GENERIC_MESSAGE));
    }
}
