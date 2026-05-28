package Application.Features.Vehicles.Commands.Update;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateVehicleCommand implements ICommand<Unit> {

    private final UUID id;
    private final String brand;
    private final String vehicleModel;
    private final VehicleType vehicleType;
    private final Integer year;
    private final String notes;
    private final UUID responsibleId;
}