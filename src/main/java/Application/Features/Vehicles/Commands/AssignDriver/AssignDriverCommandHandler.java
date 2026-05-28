package Application.Features.Vehicles.Commands.AssignDriver;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.AssignmentHistory;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DriverStatus;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.AssignmentHistoryRepository;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AssignDriverCommandHandler
        implements IRequestHandler<AssignDriverCommand, Unit> {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final AssignmentHistoryRepository assignmentHistoryRepository;

    @Override
    public Result<Unit> handle(AssignDriverCommand command) {

        Vehicle vehicle = vehicleRepository.findById(command.getVehicleId())
                .orElse(null);

        if (vehicle == null) {
            return Result.Failure("No se encontró el vehículo");
        }

        // Regla: vehículo inactivo o en mantenimiento no puede tener conductor
        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            return Result.Failure("El vehículo no está activo");
        }

        // Desasignar conductor actual si existe
        if (vehicle.getAssignedDriver() != null) {
            assignmentHistoryRepository
                    .findByVehicleIdAndEndDateIsNull(vehicle.getId())
                    .ifPresent(history -> {
                        history.setEndDate(LocalDate.now());
                        assignmentHistoryRepository.update(history);
                    });
            vehicle.getAssignedDriver().setCurrentVehicle(null);
            vehicle.setAssignedDriver(null);
        }

        // Si driverId es null solo desasignamos
        if (command.getDriverId() == null) {
            vehicleRepository.update(vehicle);
            return Result.Success();
        }

        // Busca el nuevo conductor
        Driver driver = driverRepository.findById(command.getDriverId())
                .orElse(null);

        if (driver == null) {
            return Result.Failure("No se encontró el conductor");
        }

        // Regla: conductor debe estar activo
        if (driver.getStatus() != DriverStatus.ACTIVE) {
            return Result.Failure("El conductor no está activo");
        }

        // Regla: conductor no puede estar asignado a otro vehículo
        if (driver.getCurrentVehicle() != null) {
            return Result.Failure("El conductor ya está asignado a otro vehículo");
        }

        // Asigna el conductor
        vehicle.setAssignedDriver(driver);
        driver.setCurrentVehicle(vehicle);

        // Registra en el historial
        AssignmentHistory history = AssignmentHistory.builder()
                .vehicle(vehicle)
                .driver(driver)
                .startDate(LocalDate.now())
                .build();

        assignmentHistoryRepository.saveee(history);
        vehicleRepository.update(vehicle);

        return Result.Success();
    }
}