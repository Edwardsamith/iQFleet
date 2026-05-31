package Application.Features.Users.Queries.GetByFilter;

import Application.Abstractions.IRequest;
import Application.Features.Users.Queries.UserDetailResponse;

import java.util.List;

public record GetUsersByFilterQuery(String status, String role) implements IRequest<List<UserDetailResponse>> {}
