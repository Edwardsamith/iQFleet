package Application.Features.Vehicles.Commands.Create;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateVehicleCommand implements ICommand<Unit> {

    private final String plateNumber;
    private final String brand;
    private final String vehicleModel;
    private final VehicleType vehicleType;
    private final Integer year;
    private final String notes;
    private final LocalDate registrationDate;
    private final UUID responsibleId;
}