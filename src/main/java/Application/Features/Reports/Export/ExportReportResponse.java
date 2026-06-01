package Application.Features.Reports.Export;

public record ExportReportResponse(
        String fileName,
        String format,
        String message
) {
    public ExportReportResponse(String fileName, String format, String message) {
        this.fileName = fileName;
        this.format = format;
        this.message = message;
    }
}
