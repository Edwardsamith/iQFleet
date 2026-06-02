package Application.Features.Reports.Export;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;

public class ExportReportQueryHandler implements IRequestHandler<
        ExportReportQuery,
        ExportReportResponse> {
    @Override
    public Result<ExportReportResponse>handle(ExportReportQuery command){
        return Result.Success(new ExportReportResponse("report.pdf", "Reporte exportado correctamente"));
    }
}
