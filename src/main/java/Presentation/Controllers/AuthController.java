package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Auth.Commands.Recovery.Request.RecoverPasswordCommand;
import Application.Features.Auth.Commands.Recovery.Request.RecoverPasswordResponse;
import Application.Features.Auth.Commands.Recovery.Reset.ResetPasswordCommand;
import Application.Features.Auth.Commands.Recovery.Reset.ResetPasswordResponse;
import Application.Features.Auth.Commands.Recovery.Verify.VerifyRecoveryCodeCommand;
import Application.Features.Auth.Commands.Recovery.Verify.VerifyRecoveryCodeResponse;
import Application.Features.Auth.Commands.Register.RegisterUserCommand;
import Application.Features.Auth.Commands.Register.RegisterUserResponse;
import Application.Features.Auth.Commands.SignIn.LoginCommand;
import Application.Features.Auth.Commands.SignIn.LoginResponse;
import Application.Features.Auth.Queries.GetPerfil.GetPerfilUsuarioQuery;
import Application.Features.Auth.Queries.GetPerfil.GetPerfilUsuarioResponse;
import Application.Result.Result;
import Domain.Enums.IdentificationType;
import Domain.Enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IMediator mediator;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Result<LoginResponse> result = mediator.send(
                new LoginCommand(request.email(), request.password())
        );
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PostMapping("/registro")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        IdentificationType idType;
        try {
            idType = IdentificationType.valueOf(request.identificationType());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(java.util.List.of("Tipo de identificación inválido"));
        }

        Role role;
        try {
            role = Role.valueOf(request.role());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body(java.util.List.of("Rol inválido"));
        }

        Result<RegisterUserResponse> result = mediator.send(new RegisterUserCommand(
                request.firstName(), request.lastName(), idType,
                request.identificationNumber(), request.email(), request.phone(),
                request.username(), request.password(), role
        ));

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.status(201).body(result.getValue());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("Sesión cerrada correctamente");
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordRequest request) {
        Result<RecoverPasswordResponse> result = mediator.send(
                new RecoverPasswordCommand(request.method(), request.recipient())
        );
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        Result<VerifyRecoveryCodeResponse> result = mediator.send(
                new VerifyRecoveryCodeCommand(request.email(), request.code())
        );
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Result<ResetPasswordResponse> result = mediator.send(
                new ResetPasswordCommand(request.resetToken(), request.newPassword())
        );
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        return ResponseEntity.ok(result.getValue());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = (String) authentication.getPrincipal();
        Result<GetPerfilUsuarioResponse> result = mediator.send(new GetPerfilUsuarioQuery(email));
        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.getValue());
    }

    record LoginRequest(String email, String password) {}

    record RecoverPasswordRequest(String method, String recipient) {}

    record VerifyCodeRequest(String email, String code) {}

    record ResetPasswordRequest(String resetToken, String newPassword) {}

    record RegisterRequest(
            String firstName, String lastName, String identificationType,
            String identificationNumber, String email, String phone,
            String username, String password, String role
    ) {}
}
