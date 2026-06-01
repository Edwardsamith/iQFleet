package Application.Features.Reports.Financial;

import Application.Abstractions.IQuery;

import java.time.LocalDate;
import java.util.UUID;

public record GetFinancialReportQuery(
        LocalDate startDate,
        LocalDate endDate,
        UUID vehicleId,
        String category
) implements IQuery<GetFinancialReportResponse> {
}
