package Application.Features.Vehicles.Queries.GetByFilter;

import Application.Abstractions.IQuery;
import Domain.Entities.Vehicle;
import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetVehiclesByFilterQuery implements IQuery<List<Vehicle>> {

    private final VehicleStatus status;    // null = todos
    private final VehicleType vehicleType; // null = todos
    private final Boolean withoutDriver;   // true = solo sin conductor asignado
}