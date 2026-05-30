package Application.Features.Drivers.Queries.GetByFilter;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Driver;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetDriversByFilterQueryHandler
        implements IRequestHandler<GetDriversByFilterQuery, List<Driver>> {

    private final DriverRepository driverRepository;

    @Override
    public Result<List<Driver>> handle(GetDriversByFilterQuery query) {

        // Trae todos los conductores y filtra en memoria
        List<Driver> drivers = driverRepository.findAll();

        // Filtro por estado
        if (query.getStatus() != null) {
            drivers = drivers.stream()
                    .filter(d -> d.getStatus() == query.getStatus())
                    .collect(Collectors.toList());
        }

        // Filtro por categoría de licencia
        if (query.getLicenseCategory() != null) {
            drivers = drivers.stream()
                    .filter(d -> d.getLicenseCategory() == query.getLicenseCategory())
                    .collect(Collectors.toList());
        }

        // Filtro por vencimiento próximo
        if (query.getExpiresInDays() != null) {
            LocalDate today = LocalDate.now();
            LocalDate limit = today.plusDays(query.getExpiresInDays());
            drivers = drivers.stream()
                    .filter(d -> d.getLicenseExpiry() != null
                            && !d.getLicenseExpiry().isBefore(today)
                            && !d.getLicenseExpiry().isAfter(limit))
                    .collect(Collectors.toList());
        }

        return Result.Success(drivers);
    }
}