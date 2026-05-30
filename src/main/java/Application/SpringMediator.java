package Application;


import Application.Abstractions.IMediator;
import Application.Abstractions.IRequest;
import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpringMediator implements IMediator {

    // Spring nos da acceso a todos los beans del proyecto
    private final ApplicationContext context;

    // Cache para no buscar el handler cada vez
    private final Map<Class<?>, IRequestHandler<?, ?>> cache = new ConcurrentHashMap<>();

    public SpringMediator(ApplicationContext context) {
        this.context = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TResponse> Result<TResponse> send(IRequest<TResponse> request) {

        Class<?> requestType = request.getClass();

        // Busca en cache primero, si no lo encuentra lo busca en Spring
        IRequestHandler<IRequest<TResponse>, TResponse> handler =
                (IRequestHandler<IRequest<TResponse>, TResponse>)
                        cache.computeIfAbsent(requestType, this::findHandler);

        return handler.handle(request);
    }

    private IRequestHandler<?, ?> findHandler(Class<?> requestType) {

        // Obtiene todos los beans que implementen IRequestHandler
        Map<String, IRequestHandler> allHandlers =
                context.getBeansOfType(IRequestHandler.class);

        for (IRequestHandler<?, ?> handler : allHandlers.values()) {

            // Revisa qué tipo de request maneja este handler (usando la clase real, no el proxy)
            Type[] interfaces = AopProxyUtils.ultimateTargetClass(handler).getGenericInterfaces();

            for (Type iface : interfaces) {
                if (iface instanceof ParameterizedType pt) {
                    Type[] args = pt.getActualTypeArguments();
                    if (args.length > 0 && args[0].equals(requestType)) {
                        return handler;
                    }
                }
            }
        }

        throw new RuntimeException(
                "No handler registered for: " + requestType.getName() +
                        ". ¿Olvidaste agregar @Component al handler?"
        );
    }
}
