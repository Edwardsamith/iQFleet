package Application.Features.Reports.Documentary;

public record GetDocumentaryReportResponse(
        int vigente,
        int vencidos,
        int proximosAVencer
) {
}