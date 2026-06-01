package Application.Features.Reports.Documentary;

import Application.Abstractions.IQuery;

import java.time.LocalDate;

public record GetDocumentaryReportQuery(
        LocalDate startDate,
        LocalDate endDate,
        String documentType,
        String status
) implements IQuery<GetDocumentaryReportResponse> {
}