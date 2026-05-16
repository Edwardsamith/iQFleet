
package Infrastructure.Seeder;

import Domain.Entities.Driver;
import Domain.Entities.User;
import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
import Infrastructure.Repositories.JpaVehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleSeeder {

    private final JpaVehicleRepository vehicleRepository;

    /**
     * @param users   [0]=owner, [1]=admin
     * @param drivers [0]=Carlos, [1]=Luis, [2]=María, [3]=Jorge(inactivo), [4]=Ana
     * @return [0]=bus1, [1]=microbus, [2]=van, [3]=bus2(mantenimiento), [4]=minibus
     */
    public List<Vehicle> seed(List<User> users, List<Driver> drivers) {
        User owner = users.get(0);
        User admin = users.get(1);

        Driver carlos = drivers.get(0);
        Driver luis   = drivers.get(1);
        Driver maria  = drivers.get(2);
        Driver ana    = drivers.get(4);

        List<Vehicle> vehicles = vehicleRepository.saveAll(List.of(
                Vehicle.builder()
                        .plateNumber("ABC-123")
                        .brand("Mercedes-Benz")
                        .vehicleModel("Sprinter 516")
                        .vehicleType(VehicleType.BUS)
                        .year(2022)
                        .status(VehicleStatus.ACTIVE)
                        .responsible(owner)
                        .registrationDate(LocalDate.of(2022, 3, 15))
                        .assignedDriver(carlos)
                        .notes("Bus principal de ruta norte. Revisión técnica al día.")
                        .build(),

                Vehicle.builder()
                        .plateNumber("XYZ-456")
                        .brand("Toyota")
                        .vehicleModel("HiAce Commuter")
                        .vehicleType(VehicleType.MICROBUS)
                        .year(2021)
                        .status(VehicleStatus.ACTIVE)
                        .responsible(owner)
                        .registrationDate(LocalDate.of(2021, 7, 20))
                        .assignedDriver(luis)
                        .build(),

                Vehicle.builder()
                        .plateNumber("DEF-789")
                        .brand("Ford")
                        .vehicleModel("Transit 350")
                        .vehicleType(VehicleType.VAN)
                        .year(2023)
                        .status(VehicleStatus.ACTIVE)
                        .responsible(admin)
                        .registrationDate(LocalDate.of(2023, 1, 10))
                        .assignedDriver(maria)
                        .notes("Vehículo nuevo. Garantía vigente hasta 2026.")
                        .build(),

                Vehicle.builder()
                        .plateNumber("GHI-012")
                        .brand("Chevrolet")
                        .vehicleModel("NPR 4.8")
                        .vehicleType(VehicleType.BUS)
                        .year(2019)
                        .status(VehicleStatus.UNDER_MAINTENANCE)
                        .responsible(owner)
                        .registrationDate(LocalDate.of(2019, 11, 5))
                        .notes("En taller por falla en sistema de frenos. Est. regreso: 3 días.")
                        .build(),

                Vehicle.builder()
                        .plateNumber("JKL-345")
                        .brand("Hyundai")
                        .vehicleModel("County")
                        .vehicleType(VehicleType.MINIBUS)
                        .year(2020)
                        .status(VehicleStatus.ACTIVE)
                        .responsible(admin)
                        .registrationDate(LocalDate.of(2020, 6, 28))
                        .assignedDriver(ana)
                        .build()
        ));

        log.info("{} vehículos creados.", vehicles.size());
        return vehicles;
    }
}
