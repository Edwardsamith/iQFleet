package Application.Features.Auth.Queries.GetPerfil;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetPerfilUsuarioQueryHandler
        implements IRequestHandler<GetPerfilUsuarioQuery, GetPerfilUsuarioResponse> {

    private final UserRepository userRepository;

    @Override
    public Result<GetPerfilUsuarioResponse> handle(GetPerfilUsuarioQuery query) {

        var optionalUser = userRepository.findByEmail(query.email());

        if (optionalUser.isEmpty()) {
            return Result.Failure("Usuario no encontrado");
        }

        var user = optionalUser.get();

        return Result.Success(new GetPerfilUsuarioResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getLastAccess()
        ));
    }
}
