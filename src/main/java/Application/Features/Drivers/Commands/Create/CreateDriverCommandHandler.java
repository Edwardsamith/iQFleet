package Application.Features.Drivers.Commands.Create;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CreateDriverCommandHandler
        implements IRequestHandler<CreateDriverCommand, Unit> {

    private final DriverRepository driverRepository;

    @Override
    public Result<Unit> handle(CreateDriverCommand command) {

        // Regla de negocio 1: no puede existir otro conductor con la misma cédula
        if (driverRepository.existsByIdentificationNumber(
                command.getIdentificationNumber())) {
            return Result.Failure("Ya existe un conductor con ese número de identificación");
        }

        // Regla de negocio 2: no puede existir otro conductor con la misma licencia
        if (driverRepository.existsByLicenseNumber(
                command.getLicenseNumber())) {
            return Result.Failure("Ya existe un conductor con ese número de licencia");
        }

        // Construye el conductor con el patrón Builder de Lombok
        Driver driver = Driver.builder()
                .identificationType(command.getIdentificationType())
                .identificationNumber(command.getIdentificationNumber())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .licenseNumber(command.getLicenseNumber())
                .licenseCategory(command.getLicenseCategory())
                .licenseExpiry(command.getLicenseExpiry())
                .phone(command.getPhone())
                .email(command.getEmail())
                .address(command.getAddress())
                .status(DriverStatus.ACTIVE)
                .registrationDate(command.getRegistrationDate() != null
                        ? command.getRegistrationDate()
                        : LocalDate.now())
                .build();

        driverRepository.save(driver);

        return Result.Success();
    }
}