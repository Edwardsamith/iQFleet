package Application.Abstractions;

import Application.Result.Result;

public interface IMediator {

    <TResponse> Result<TResponse> send(IRequest<TResponse> request);
}