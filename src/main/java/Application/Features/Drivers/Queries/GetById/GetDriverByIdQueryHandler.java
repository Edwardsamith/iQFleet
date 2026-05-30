package Application.Features.Drivers.Queries.GetById;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Driver;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetDriverByIdQueryHandler
        implements IRequestHandler<GetDriverByIdQuery, Driver> {

    private final DriverRepository driverRepository;

    @Override
    public Result<Driver> handle(GetDriverByIdQuery query) {

        Driver driver = driverRepository.findById(query.getId())
                .orElse(null);

        if (driver == null) {
            return Result.Failure("No se encontró un conductor con ese ID");
        }

        return Result.Success(driver);
    }
}