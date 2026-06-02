package Application.Features.Reports.Export;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExportReportResponse {
    private final String fileName;
    private final String contentType;
    private final byte[] content;
    private final String message;
}
