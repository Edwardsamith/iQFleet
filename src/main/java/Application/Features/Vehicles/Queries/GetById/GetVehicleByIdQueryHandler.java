package Application.Features.Vehicles.Queries.GetById;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Vehicle;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetVehicleByIdQueryHandler
        implements IRequestHandler<GetVehicleByIdQuery, Vehicle> {

    private final VehicleRepository vehicleRepository;

    @Override
    public Result<Vehicle> handle(GetVehicleByIdQuery query) {

        Vehicle vehicle = vehicleRepository.findById(query.getId())
                .orElse(null);

        if (vehicle == null) {
            return Result.Failure("No se encontró un vehículo con ese ID");
        }

        return Result.Success(vehicle);
    }
}