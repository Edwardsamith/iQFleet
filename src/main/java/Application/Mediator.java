package Application;

import Application.Abstractions.IRequestHandler;
import Application.Abstractions.IMediator;
import Application.Abstractions.IRequest;
import Application.Result.Result;

import java.util.HashMap;
import java.util.Map;

public class Mediator implements IMediator {

    private final Map<Class<?>, IRequestHandler<?, ?>> handlers = new HashMap<>();

    public <TRequest extends IRequest<TResponse>, TResponse>

    void registerHandler( Class<TRequest> requestType, IRequestHandler<TRequest, TResponse> handler) {

        handlers.put(requestType, handler);
    }



    @Override
    @SuppressWarnings("unchecked")
    public <TResponse> Result<TResponse> send(
            IRequest<TResponse> request
    ) {

        IRequestHandler<IRequest<TResponse>, TResponse> handler = (IRequestHandler<IRequest<TResponse>, TResponse>)

                handlers.get(request.getClass());

        if (handler == null) {

            throw new RuntimeException( "No handler registered for: " + request.getClass().getName() );
        }

        return handler.handle(request);
    }
}