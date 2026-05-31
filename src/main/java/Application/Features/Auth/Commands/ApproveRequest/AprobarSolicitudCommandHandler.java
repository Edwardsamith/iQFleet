package Application.Features.Auth.Commands.ApproveRequest;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.RegistrationRequest;
import Domain.Entities.User;
import Domain.Enums.DecisionStatus;
import Domain.Enums.UserStatus;
import Domain.Repositories.RegistrationRequestRepository;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AprobarSolicitudCommandHandler
        implements IRequestHandler<AprobarSolicitudCommand, AprobarSolicitudResponse> {

    private final RegistrationRequestRepository solicitudRepository;
    private final UserRepository userRepository;

    @Override
    public Result<AprobarSolicitudResponse> handle(AprobarSolicitudCommand command) {

        var optionalSolicitud = solicitudRepository.findById(command.solicitudId());
        if (optionalSolicitud.isEmpty()) {
            return Result.Failure("Solicitud no encontrada");
        }

        RegistrationRequest solicitud = optionalSolicitud.get();

        if (solicitud.getDecision() != null) {
            return Result.Failure("La solicitud ya fue procesada");
        }

        var optionalUser = userRepository.findById(solicitud.getUserId());
        if (optionalUser.isEmpty()) {
            return Result.Failure("Usuario de la solicitud no encontrado");
        }

        User user = optionalUser.get();

        if (command.rolAsignado() != null) {
            user.setRole(command.rolAsignado());
        }
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        solicitud.setDecision(DecisionStatus.APPROVED);
        solicitud.setReviewedById(command.adminId());
        solicitud.setDecisionDate(LocalDateTime.now());
        solicitudRepository.save(solicitud);

        return Result.Success(new AprobarSolicitudResponse(
                solicitud.getId(),
                user.getId(),
                "Solicitud aprobada. El usuario ahora puede iniciar sesión."
        ));
    }
}
