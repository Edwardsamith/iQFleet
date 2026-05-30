package Application.Features.Vehicles.Commands.Create;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.User;
import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.UserRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CreateVehicleCommandHandler
        implements IRequestHandler<CreateVehicleCommand, Unit> {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public Result<Unit> handle(CreateVehicleCommand command) {

        // Regla de negocio: la placa debe ser única
        if (vehicleRepository.existsByPlateNumber(command.getPlateNumber())) {
            return Result.Failure("Ya existe un vehículo con esa placa");
        }

        // Busca el responsable si se proporcionó
        User responsible = null;
        if (command.getResponsibleId() != null) {
            responsible = userRepository.findById(command.getResponsibleId())
                    .orElse(null);
            if (responsible == null) {
                return Result.Failure("No se encontró el usuario responsable");
            }
        }

        Vehicle vehicle = Vehicle.builder()
                .plateNumber(command.getPlateNumber())
                .brand(command.getBrand())
                .vehicleModel(command.getVehicleModel())
                .vehicleType(command.getVehicleType())
                .year(command.getYear())
                .status(VehicleStatus.ACTIVE)
                .responsible(responsible)
                .notes(command.getNotes())
                .registrationDate(command.getRegistrationDate() != null
                        ? command.getRegistrationDate()
                        : LocalDate.now())
                .build();

        vehicleRepository.saveee(vehicle);

        return Result.Success();
    }
}