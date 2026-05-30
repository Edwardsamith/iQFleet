package Application.Features.Drivers.Commands.Update;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Driver;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateDriverCommandHandler
        implements IRequestHandler<UpdateDriverCommand, Unit> {

    private final DriverRepository driverRepository;

    @Override
    public Result<Unit> handle(UpdateDriverCommand command) {

        // Busca el conductor por ID
        Driver driver = driverRepository.findById(command.getId())
                .orElse(null);

        // Regla de negocio: el conductor debe existir
        if (driver == null) {
            return Result.Failure("No se encontró un conductor con ese ID");
        }

        // Actualiza solo los campos modificables
        driver.setFirstName(command.getFirstName());
        driver.setLastName(command.getLastName());
        driver.setLicenseCategory(command.getLicenseCategory());
        driver.setLicenseExpiry(command.getLicenseExpiry());
        driver.setPhone(command.getPhone());
        driver.setEmail(command.getEmail());
        driver.setAddress(command.getAddress());

        driverRepository.update(driver);

        return Result.Success();
    }
}