package Application.Features.Reports.Operational;

import Application.Abstractions.IQuery;

import java.time.LocalDate;
import java.util.UUID;

public record GetOperationalReportQuery(
        LocalDate startDate,
        LocalDate endDate,
        UUID vehicleId,
        UUID driverId,
        String status
) implements IQuery<GetOperationalReportResponse> {
}