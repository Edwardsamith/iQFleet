package Application.Features.Reports.Operational;

import Application.Abstractions.IQuery;

public record GetOperationalReportQuery()
        implements IQuery<GetOperationalReportResponse> {
}