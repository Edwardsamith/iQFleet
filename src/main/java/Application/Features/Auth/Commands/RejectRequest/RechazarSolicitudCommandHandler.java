package Application.Features.Auth.Commands.RejectRequest;

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
public class RechazarSolicitudCommandHandler
        implements IRequestHandler<RechazarSolicitudCommand, RechazarSolicitudResponse> {

    private final RegistrationRequestRepository solicitudRepository;
    private final UserRepository userRepository;

    @Override
    public Result<RechazarSolicitudResponse> handle(RechazarSolicitudCommand command) {

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
        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        solicitud.setDecision(DecisionStatus.REJECTED);
        solicitud.setRejectionReason(command.motivoRechazo());
        solicitud.setReviewedById(command.adminId());
        solicitud.setDecisionDate(LocalDateTime.now());
        solicitudRepository.save(solicitud);

        return Result.Success(new RechazarSolicitudResponse(
                solicitud.getId(),
                user.getId(),
                "Solicitud rechazada. El usuario no podrá iniciar sesión."
        ));
    }
}
