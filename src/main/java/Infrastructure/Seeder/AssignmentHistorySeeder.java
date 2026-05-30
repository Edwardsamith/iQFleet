package Infrastructure.Seeder;

import Domain.Entities.AssignmentHistory;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Repositories.AssignmentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssignmentHistorySeeder {

    private final AssignmentHistoryRepository assignmentHistoryRepository;

    /**
     * @param drivers  [0]=Carlos, [1]=Luis, [2]=María, [3]=Jorge, [4]=Ana
     * @param vehicles [0]=bus1, [1]=microbus, [2]=van, [3]=bus2, [4]=minibus
     */
    public void seed(List<Driver> drivers, List<Vehicle> vehicles) {
        Driver carlos = drivers.get(0);
        Driver luis   = drivers.get(1);
        Driver maria  = drivers.get(2);
        Driver jorge  = drivers.get(3);
        Driver ana    = drivers.get(4);

        Vehicle bus1     = vehicles.get(0);
        Vehicle microbus = vehicles.get(1);
        Vehicle van      = vehicles.get(2);
        Vehicle bus2     = vehicles.get(3);
        Vehicle minibus  = vehicles.get(4);

        // Asignación histórica: Ana en bus2 antes de que fuera a mantenimiento
        assignmentHistoryRepository.save(AssignmentHistory.builder()
                .vehicleId(bus2.getId())
                .driverId(ana.getId())
                .startDate(LocalDate.now().minusYears(3))
                .endDate(LocalDate.now().minusYears(2))
                .build());

        // Asignación histórica: Jorge en bus2 hasta su desactivación
        assignmentHistoryRepository.save(AssignmentHistory.builder()
                .vehicleId(bus2.getId())
                .driverId(jorge.getId())
                .startDate(LocalDate.now().minusYears(2))
                .endDate(LocalDate.now().minusMonths(2))
                .build());

        // Asignaciones activas (sin fecha fin)
        assignmentHistoryRepository.save(AssignmentHistory.builder()
                .vehicleId(bus1.getId())
                .driverId(carlos.getId())
                .startDate(LocalDate.now().minusYears(1))
                .build());

        assignmentHistoryRepository.save(AssignmentHistory.builder()
                .vehicleId(microbus.getId())
                .driverId(luis.getId())
                .startDate(LocalDate.now().minusYears(1).minusMonths(3))
                .build());

        assignmentHistoryRepository.save(AssignmentHistory.builder()
                .vehicleId(van.getId())
                .driverId(maria.getId())
                .startDate(LocalDate.of(2023, 2, 1))
                .build());

        assignmentHistoryRepository.save(AssignmentHistory.builder()
                .vehicleId(minibus.getId())
                .driverId(ana.getId())
                .startDate(LocalDate.now().minusMonths(6))
                .build());

        log.info("Historial de asignaciones creado.");
    }
}
