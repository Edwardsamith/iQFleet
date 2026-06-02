package Application.Features.Reports.Export;

import Application.Abstractions.IMediator;
import Application.Abstractions.IRequestHandler;
import Application.Features.Reports.Documentary.GetDocumentaryReportQuery;
import Application.Features.Reports.Documentary.GetDocumentaryReportResponse;
import Application.Features.Reports.Financial.GetFinancialReportQuery;
import Application.Features.Reports.Financial.GetFinancialReportResponse;
import Application.Features.Reports.Operational.GetOperationalReportQuery;
import Application.Features.Reports.Operational.GetOperationalReportResponse;
import Application.Result.Result;
import Application.Services.ExportReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExportReportQueryHandler
        implements IRequestHandler<ExportReportQuery, ExportReportResponse> {

    private final IMediator mediator;
    private final ExportReportService exportService;

    @Override
    public Result<ExportReportResponse> handle(ExportReportQuery query) {

        String formatStr = query.getFormat() == null ? "CSV" : query.getFormat().toUpperCase();
        if (!formatStr.equals("PDF") && !formatStr.equals("XLSX") && !formatStr.equals("CSV"))
            return Result.Failure("Formato no soportado. Use: PDF, XLSX o CSV");

        try {
            return switch (query.getReportType().toUpperCase()) {
                case "FINANCIAL"   -> exportFinancial(query, formatStr);
                case "DOCUMENTARY" -> exportDocumentary(query, formatStr);
                case "OPERATIONAL" -> exportOperational(query, formatStr);
                default -> Result.Failure("Tipo de reporte no válido. Use: FINANCIERO, DOCUMENTAL u OPERATIVO");
            };
        } catch (Exception e) {
            return Result.Failure("Error al generar el archivo: " + e.getMessage());
        }
    }

    private Result<ExportReportResponse> exportFinancial(ExportReportQuery q, String formatStr) throws Exception {
        Result<GetFinancialReportResponse> result = mediator.send(new GetFinancialReportQuery(
                q.getSubType(), q.getFromDate(), q.getToDate(),
                q.getVehicleId(), q.getDriverId(), q.getMovementCategory(),
                q.getMovementType(), q.getPaymentMethod(), q.getGeneratedBy()));
        if (!result.isSuccess()) return Result.Failure(result.getErrors());
        byte[] bytes = exportService.exportFinancial(result.getValue(), formatStr);
        return Result.Success(new ExportReportResponse(
                buildFileName("financiero-" + q.getSubType().toLowerCase(), formatStr),
                resolveContentType(formatStr), bytes, "Reporte exportado correctamente"));
    }

    private Result<ExportReportResponse> exportDocumentary(ExportReportQuery q, String formatStr) throws Exception {
        Result<GetDocumentaryReportResponse> result = mediator.send(new GetDocumentaryReportQuery(
                q.getSubType(), q.getDocumentType(), q.getDocumentStatus(),
                q.getVehicleId(), q.getDriverId(), q.getDaysToExpire(), q.getGeneratedBy()));
        if (!result.isSuccess()) return Result.Failure(result.getErrors());
        byte[] bytes = exportService.exportDocumentary(result.getValue(), formatStr);
        return Result.Success(new ExportReportResponse(
                buildFileName("documental-" + q.getSubType().toLowerCase(), formatStr),
                resolveContentType(formatStr), bytes, "Reporte exportado correctamente"));
    }

    private Result<ExportReportResponse> exportOperational(ExportReportQuery q, String formatStr) throws Exception {
        Result<GetOperationalReportResponse> result = mediator.send(new GetOperationalReportQuery(
                q.getSubType(), q.getVehicleStatus(), q.getVehicleType(),
                q.getDriverStatus(), q.getLicenseCategory(),
                q.getVehicleId(), q.getDriverId(),
                q.getFromDate(), q.getToDate(), q.getGeneratedBy()));
        if (!result.isSuccess()) return Result.Failure(result.getErrors());
        byte[] bytes = exportService.exportOperational(result.getValue(), formatStr);
        return Result.Success(new ExportReportResponse(
                buildFileName("operativo-" + q.getSubType().toLowerCase(), formatStr),
                resolveContentType(formatStr), bytes, "Reporte exportado correctamente"));
    }

    private String buildFileName(String base, String formatStr) {
        return "iQFleet-" + base + "-" + System.currentTimeMillis() + "." + formatStr.toLowerCase();
    }

    private String resolveContentType(String formatStr) {
        return switch (formatStr) {
            case "PDF"  -> "application/pdf";
            case "XLSX" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default     -> "text/csv;charset=UTF-8";
        };
    }
}
