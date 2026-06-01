package Application.Features.Reports.Operational;

public record GetOperationalReportResponse(
        int totalVehicles,
        int totalDrivers
) {
}
