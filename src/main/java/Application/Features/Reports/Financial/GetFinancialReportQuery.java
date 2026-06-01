package Application.Features.Reports.Financial;

import Application.Abstractions.IQuery;

public record GetFinancialReportQuery(
        String startDate,
        String endDate
) implements IQuery<GetFinancialReportResponse> {
}
