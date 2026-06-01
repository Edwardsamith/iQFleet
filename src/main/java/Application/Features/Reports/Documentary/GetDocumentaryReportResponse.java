package Application.Features.Reports.Documentary;

public record GetDocumentaryReportResponse(
        int vigentes,
        int proximosAVencer,
        int vencidos
) {
}