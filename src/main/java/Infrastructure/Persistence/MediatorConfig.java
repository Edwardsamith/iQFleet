package Infrastructure.Persistence;

import Application.SpringMediator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MediatorConfig {

    // Le dice a Spring que cuando alguien pida un IMediator,
    // use el SpringMediator en lugar del Mediator original
    @Bean
    @Primary
    public SpringMediator mediator(SpringMediator springMediator) {
        return springMediator;
    }
}