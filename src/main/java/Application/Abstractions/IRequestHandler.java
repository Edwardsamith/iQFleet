package Application.Abstractions;

import Application.Abstractions.IRequest;
import Application.Result.Result;

public interface IRequestHandler
        <TRequest extends IRequest<TResponse>, TResponse> {

    Result<TResponse> handle(TRequest request);
}