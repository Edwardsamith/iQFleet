package Application.Features.Users.Queries.GetById;

import Application.Abstractions.IRequest;
import Application.Features.Users.Queries.UserDetailResponse;

import java.util.UUID;

public record GetUserByIdQuery(UUID userId) implements IRequest<UserDetailResponse> {}
