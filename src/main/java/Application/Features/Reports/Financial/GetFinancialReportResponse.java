package Application.Features.Reports.Financial;

public record GetFinancialReportResponse(
        double ingresos,
        double egresos,
        double balance
) {
}
