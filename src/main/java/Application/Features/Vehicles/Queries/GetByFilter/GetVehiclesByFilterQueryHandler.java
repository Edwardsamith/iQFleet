package Application.Features.Vehicles.Queries.GetByFilter;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Vehicle;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetVehiclesByFilterQueryHandler
        implements IRequestHandler<GetVehiclesByFilterQuery, List<Vehicle>> {

    private final VehicleRepository vehicleRepository;

    @Override
    public Result<List<Vehicle>> handle(GetVehiclesByFilterQuery query) {

        List<Vehicle> vehicles = vehicleRepository.findAll();

        if (query.getStatus() != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getStatus() == query.getStatus())
                    .collect(Collectors.toList());
        }

        if (query.getVehicleType() != null) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getVehicleType() == query.getVehicleType())
                    .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(query.getWithoutDriver())) {
            vehicles = vehicles.stream()
                    .filter(v -> v.getAssignedDriver() == null)
                    .collect(Collectors.toList());
        }

        return Result.Success(vehicles);
    }
}
