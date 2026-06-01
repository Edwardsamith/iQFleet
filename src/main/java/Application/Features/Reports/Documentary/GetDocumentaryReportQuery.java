package Application.Features.Reports.Documentary;

import Application.Abstractions.IQuery;

public record GetDocumentaryReportQuery(
        String documentType
) implements IQuery<GetDocumentaryReportResponse> {
}
