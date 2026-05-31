package Application.Features.Vehicles.Commands.ChangeStatus;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.AssignmentHistory;
import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.AssignmentHistoryRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ChangeVehicleStatusCommandHandler
        implements IRequestHandler<ChangeVehicleStatusCommand, Unit> {

    private final VehicleRepository vehicleRepository;
    private final AssignmentHistoryRepository assignmentHistoryRepository;

    @Override
    public Result<Unit> handle(ChangeVehicleStatusCommand command) {

        Vehicle vehicle = vehicleRepository.findById(command.getId())
                .orElse(null);

        if (vehicle == null) {
            return Result.Failure("No se encontró un vehículo con ese ID");
        }

        // Regla de negocio RF-002: si el vehículo entra a mantenimiento
        // o se inactiva, se desasigna el conductor automáticamente
        if ((command.getStatus() == VehicleStatus.UNDER_MAINTENANCE
                || command.getStatus() == VehicleStatus.INACTIVE)
                && vehicle.getAssignedDriverId() != null) {

            // Cierra la asignación activa en el historial
            assignmentHistoryRepository
                    .findByVehicleIdAndEndDateIsNull(vehicle.getId())
                    .ifPresent(history -> {
                        history.setEndDate(LocalDate.now());
                        assignmentHistoryRepository.save(history);
                    });

            // Desasigna el conductor
            vehicle.setAssignedDriverId(null);
        }

        vehicle.setStatus(command.getStatus());
        vehicleRepository.save(vehicle);

        return Result.Success();
    }
}
