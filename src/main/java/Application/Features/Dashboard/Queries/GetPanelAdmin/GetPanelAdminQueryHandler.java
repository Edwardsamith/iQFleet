package Application.Features.Dashboard.Queries.GetPanelAdmin;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.User;
import Domain.Enums.UserStatus;
import Domain.Repositories.RegistrationRequestRepository;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetPanelAdminQueryHandler
        implements IRequestHandler<GetPanelAdminQuery, PanelAdminResponse> {

    private final UserRepository userRepository;
    private final RegistrationRequestRepository registrationRequestRepository;

    @Override
    public Result<PanelAdminResponse> handle(GetPanelAdminQuery query) {
        List<User> allUsers = userRepository.findAll();

        int totalUsuarios    = allUsers.size();
        int usuariosActivos  = (int) allUsers.stream().filter(u -> u.getStatus() == UserStatus.ACTIVE).count();
        int usuariosInactivos = (int) allUsers.stream().filter(u -> u.getStatus() == UserStatus.INACTIVE).count();
        int usuariosBloqueados = (int) allUsers.stream().filter(u -> u.getStatus() == UserStatus.BLOCKED).count();
        int solicitudesPendientes = registrationRequestRepository.findByDecisionIsNull().size();

        List<UltimoAccesoItem> ultimosAccesos = allUsers.stream()
                .filter(u -> u.getLastAccess() != null)
                .sorted(Comparator.comparing(User::getLastAccess).reversed())
                .limit(5)
                .map(u -> UltimoAccesoItem.builder()
                        .usuarioId(u.getId().toString())
                        .nombre(u.getFirstName() + " " + u.getLastName())
                        .rol(u.getRole().name())
                        .ultimoAcceso(u.getLastAccess())
                        .build())
                .collect(Collectors.toList());

        return Result.Success(PanelAdminResponse.builder()
                .totalUsuarios(totalUsuarios)
                .usuariosActivos(usuariosActivos)
                .usuariosInactivos(usuariosInactivos)
                .usuariosBloqueados(usuariosBloqueados)
                .solicitudesPendientes(solicitudesPendientes)
                .ultimosAccesos(ultimosAccesos)
                .build());
    }
}
