package Application.Features.Drivers.Queries.GetByFilter;

import Application.Abstractions.IQuery;
import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;
import Domain.Enums.LicenseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetDriversByFilterQuery implements IQuery<List<Driver>> {

    private final DriverStatus status;        // null = todos
    private final LicenseCategory licenseCategory; // null = todas
    private final Integer expiresInDays;      // null = sin filtro de vencimiento
}