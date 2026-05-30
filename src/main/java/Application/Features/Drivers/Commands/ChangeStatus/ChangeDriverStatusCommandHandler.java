package Application.Features.Drivers.Commands.ChangeStatus;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Driver;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangeDriverStatusCommandHandler
        implements IRequestHandler<ChangeDriverStatusCommand, Unit> {

    private final DriverRepository driverRepository;

    @Override
    public Result<Unit> handle(ChangeDriverStatusCommand command) {

        // Busca el conductor
        Driver driver = driverRepository.findById(command.getId())
                .orElse(null);

        if (driver == null) {
            return Result.Failure("No se encontró un conductor con ese ID");
        }

        // Regla de negocio: si se desactiva y tiene vehículo asignado,
        // se desasigna automáticamente
        if (command.getStatus() == Domain.Enums.DriverStatus.INACTIVE
                && driver.getCurrentVehicle() != null) {

            driver.getCurrentVehicle().setAssignedDriver(null);
            driver.setCurrentVehicle(null);
        }

        driver.setStatus(command.getStatus());
        driverRepository.update(driver);

        return Result.Success();
    }
}