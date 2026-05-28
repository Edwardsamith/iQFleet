package Application;

import Application.Abstractions.IRequestHandler;
import Application.Abstractions.IMediator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;

import java.util.Map;

@Configuration
public class MediatorConfiguration {

    @Bean
    public IMediator mediator(ApplicationContext context) {
        Mediator mediator = new Mediator();
        Map<String, IRequestHandler> handlers = context.getBeansOfType(IRequestHandler.class);
        for (IRequestHandler<?, ?> handler : handlers.values()) {
            registerHandler(mediator, handler);
        }
        return mediator;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerHandler(Mediator mediator, IRequestHandler handler) {
        ResolvableType type = ResolvableType.forClass(handler.getClass()).as(IRequestHandler.class);
        Class<?> requestType = type.getGeneric(0).resolve();
        if (requestType != null) {
            mediator.registerHandler((Class) requestType, handler);
        }
    }
}
