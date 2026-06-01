package Application.Features.Reports.Operational;

public record GetOperationalReportResponse(
        int totalVehiculos,
        int totalConductores,
        int indicadoresOperativos
) {
}
