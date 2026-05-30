package Application.Features.Vehicles.Queries.GetById;

import Application.Abstractions.IQuery;
import Domain.Entities.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetVehicleByIdQuery implements IQuery<Vehicle> {

    private final UUID id;
}