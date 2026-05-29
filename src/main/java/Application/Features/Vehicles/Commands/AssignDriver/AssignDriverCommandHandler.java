package Application.Features.Vehicles.Commands.AssignDriver;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.AssignmentHistory;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DriverStatus;
import Domain.Enums.LicenseCategory;
import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
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

        // Regla de negocio: solo vehículos activos pueden tener conductor
        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            return Result.Failure("Solo se puede asignar conductor a vehículos activos");
        }

        // Si driverId es null, es una desasignación
        if (command.getDriverId() == null) {
            return unassignDriver(vehicle);
        }

        return assignDriver(vehicle, command);
    }

    private Result<Unit> assignDriver(Vehicle vehicle, AssignDriverCommand command) {

        Driver driver = driverRepository.findById(command.getDriverId())
                .orElse(null);

        if (driver == null) {
            return Result.Failure("No se encontró el conductor");
        }

        // Regla de negocio: el conductor debe estar activo
        if (driver.getStatus() != DriverStatus.ACTIVE) {
            return Result.Failure("El conductor debe estar activo para ser asignado");
        }

        // Regla de negocio: el conductor no puede estar en otro vehículo
        if (driver.getCurrentVehicle() != null) {
            return Result.Failure("El conductor ya está asignado a otro vehículo");
        }

        // Regla de negocio: la licencia debe ser compatible con el tipo de vehículo
        if (!isLicenseCompatible(driver.getLicenseCategory(), vehicle.getVehicleType())) {
            return Result.Failure(
                    "La categoría de licencia " + driver.getLicenseCategory() +
                            " no es compatible con vehículos de tipo " + vehicle.getVehicleType()
            );
        }

        // Cierra asignación anterior si existe
        assignmentHistoryRepository
                .findByVehicleIdAndEndDateIsNull(vehicle.getId())
                .ifPresent(history -> {
                    history.setEndDate(LocalDate.now());
                    assignmentHistoryRepository.update(history);
                });

        // Crea nueva asignación en el historial
        AssignmentHistory history = AssignmentHistory.builder()
                .vehicle(vehicle)
                .driver(driver)
                .startDate(LocalDate.now())
                .build();
        assignmentHistoryRepository.saveee(history);

        // Actualiza la relación bidireccional
        vehicle.setAssignedDriver(driver);
        vehicleRepository.update(vehicle);

        return Result.Success();
    }

    private Result<Unit> unassignDriver(Vehicle vehicle) {

        if (vehicle.getAssignedDriver() == null) {
            return Result.Failure("El vehículo no tiene conductor asignado");
        }

        // Cierra la asignación activa
        assignmentHistoryRepository
                .findByVehicleIdAndEndDateIsNull(vehicle.getId())
                .ifPresent(history -> {
                    history.setEndDate(LocalDate.now());
                    assignmentHistoryRepository.update(history);
                });

        vehicle.getAssignedDriver().setCurrentVehicle(null);
        vehicle.setAssignedDriver(null);
        vehicleRepository.update(vehicle);

        return Result.Success();
    }

    // ─── Compatibilidad licencia / tipo de vehículo ───────────────────
    private boolean isLicenseCompatible(LicenseCategory license, VehicleType vehicleType) {
        return switch (vehicleType) {
            case BUS, MINIBUS -> license == LicenseCategory.C1
                    || license == LicenseCategory.C2
                    || license == LicenseCategory.C3;
            case MICROBUS     -> license == LicenseCategory.C1
                    || license == LicenseCategory.C2
                    || license == LicenseCategory.C3
                    || license == LicenseCategory.B3;
            case VAN          -> license == LicenseCategory.B1
                    || license == LicenseCategory.B2
                    || license == LicenseCategory.B3
                    || license == LicenseCategory.C1
                    || license == LicenseCategory.C2
                    || license == LicenseCategory.C3;
            case TAXI         -> license == LicenseCategory.B1
                    || license == LicenseCategory.B2
                    || license == LicenseCategory.B3;
        };
    }
}