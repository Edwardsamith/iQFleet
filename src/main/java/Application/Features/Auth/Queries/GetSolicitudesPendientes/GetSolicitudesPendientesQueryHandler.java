package Application.Features.Auth.Queries.GetSolicitudesPendientes;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.RegistrationRequest;
import Domain.Entities.User;
import Domain.Repositories.RegistrationRequestRepository;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetSolicitudesPendientesQueryHandler
        implements IRequestHandler<GetSolicitudesPendientesQuery, List<SolicitudPendienteResponse>> {

    private final RegistrationRequestRepository solicitudRepository;
    private final UserRepository userRepository;

    @Override
    public Result<List<SolicitudPendienteResponse>> handle(GetSolicitudesPendientesQuery query) {

        List<RegistrationRequest> pendientes = solicitudRepository.findByDecisionIsNull();

        List<SolicitudPendienteResponse> response = pendientes.stream()
                .map(solicitud -> {
                    Optional<User> optUser = userRepository.findById(solicitud.getUserId());
                    if (optUser.isEmpty()) return null;
                    User user = optUser.get();
                    return new SolicitudPendienteResponse(
                            solicitud.getId(),
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getUsername(),
                            user.getIdentificationType(),
                            user.getIdentificationNumber(),
                            user.getPhone(),
                            user.getRole(),
                            user.getCreatedAt()
                    );
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());

        return Result.Success(response);
    }
}
