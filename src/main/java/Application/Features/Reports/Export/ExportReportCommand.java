package Application.Features.Reports.Export;

import Application.Abstractions.ICommand;

public record ExportReportCommand(
        String reportType,
        String format
) implements ICommand<ExportReportResponse> {
}