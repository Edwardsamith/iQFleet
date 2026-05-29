package Infrastructure.Seeder;

import Domain.Entities.Driver;
import Domain.Entities.User;
import Domain.Entities.Vehicle;
import Domain.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;

    private final UserSeeder               userSeeder;
    private final DriverSeeder             driverSeeder;
    private final VehicleSeeder            vehicleSeeder;
    private final DocumentSeeder           documentSeeder;
    private final AssignmentHistorySeeder  assignmentHistorySeeder;
    private final FinancialMovementSeeder  financialMovementSeeder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!userRepository.findAll().isEmpty()) {
            log.info("Base de datos ya inicializada, omitiendo seeder.");
            return;
        }

        log.info("=== Iniciando seeder de iQFleet ===");

        List<User>    users    = userSeeder.seed();
        List<Driver>  drivers  = driverSeeder.seed();
        List<Vehicle> vehicles = vehicleSeeder.seed(users, drivers);

        documentSeeder.seed(drivers, vehicles);
        assignmentHistorySeeder.seed(drivers, vehicles);
        financialMovementSeeder.seed(drivers, vehicles);

        log.info("=== Seeder completado exitosamente ===");
    }
}
