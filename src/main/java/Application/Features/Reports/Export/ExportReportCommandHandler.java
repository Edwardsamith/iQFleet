package Application.Features.Reports.Export;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;

public class ExportReportCommandHandler implements IRequestHandler<
        ExportReportCommand,
        ExportReportResponse> {
    @Override
    public Result<ExportReportResponse>handle(ExportReportCommand command){
        return Result.Success(new ExportReportResponse("report.", "Reporte exportado correctamente", "mssg"));
    }
}
