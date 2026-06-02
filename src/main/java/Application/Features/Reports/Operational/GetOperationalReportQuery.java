package Application.Features.Reports.Operational;

import Application.Abstractions.IQuery;
import Domain.Enums.DriverStatus;
import Domain.Enums.LicenseCategory;
import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetOperationalReportQuery implements IQuery<GetOperationalReportResponse> {

    private final String subType;

    private final VehicleStatus vehicleStatus;
    private final VehicleType vehicleType;
    private final DriverStatus driverStatus;
    private final LicenseCategory driverCategory;
    private final UUID vehicleId;
    private final UUID driverId;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String generatedBy;
}