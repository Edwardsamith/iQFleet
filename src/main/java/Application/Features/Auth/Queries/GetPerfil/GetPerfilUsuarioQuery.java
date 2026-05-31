package Application.Features.Auth.Queries.GetPerfil;

import Application.Abstractions.IQuery;

public record GetPerfilUsuarioQuery(String email) implements IQuery<GetPerfilUsuarioResponse> {}
