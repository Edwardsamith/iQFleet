package Application.Features.Vehicles.Commands.Update;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.User;
import Domain.Entities.Vehicle;
import Domain.Repositories.UserRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateVehicleCommandHandler
        implements IRequestHandler<UpdateVehicleCommand, Unit> {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public Result<Unit> handle(UpdateVehicleCommand command) {

        Vehicle vehicle = vehicleRepository.findById(command.getId())
                .orElse(null);

        if (vehicle == null) {
            return Result.Failure("No se encontró un vehículo con ese ID");
        }

        // Busca el responsable si se proporcionó
        if (command.getResponsibleId() != null) {
            User responsible = userRepository.findById(command.getResponsibleId())
                    .orElse(null);
            if (responsible == null) {
                return Result.Failure("No se encontró el usuario responsable");
            }
            vehicle.setResponsible(responsible);
        }

        // Actualiza solo los campos modificables
        // La placa nunca se modifica — regla de negocio RF-002
        vehicle.setBrand(command.getBrand());
        vehicle.setVehicleModel(command.getVehicleModel());
        vehicle.setVehicleType(command.getVehicleType());
        vehicle.setYear(command.getYear());
        vehicle.setNotes(command.getNotes());

        vehicleRepository.update(vehicle);

        return Result.Success();
    }
}