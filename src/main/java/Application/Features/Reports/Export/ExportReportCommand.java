package Application.Features.Reports.Export;

import Application.Abstractions.ICommand;

public record ExportReportCommand(
        String reportType
) implements ICommand<ExportReportResponse> {
}