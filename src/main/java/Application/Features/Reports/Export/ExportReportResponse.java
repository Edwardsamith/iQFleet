package Application.Features.Reports.Export;

public record ExportReportResponse(
        String fileName,
        String format,
        String message
) {
}
