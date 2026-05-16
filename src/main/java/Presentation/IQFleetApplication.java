package Presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "Presentation",
        "Application",
        "Infrastructure"
})
@EntityScan(basePackages = "Domain.Entities")
@EnableJpaRepositories(basePackages = "Infrastructure.Repositories")
public class IQFleetApplication {

    public static void main(String[] args) {
        SpringApplication.run(IQFleetApplication.class, args);
    }
}
