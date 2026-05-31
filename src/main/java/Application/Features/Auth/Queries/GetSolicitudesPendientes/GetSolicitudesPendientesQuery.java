package Application.Features.Auth.Queries.GetSolicitudesPendientes;

import Application.Abstractions.IQuery;

import java.util.List;

public record GetSolicitudesPendientesQuery() implements IQuery<List<SolicitudPendienteResponse>> {}
