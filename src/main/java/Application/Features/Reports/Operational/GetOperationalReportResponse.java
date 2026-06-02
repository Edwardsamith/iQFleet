package Application.Features.Reports.Operational;

import Domain.Enums.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class GetOperationalReportResponse {


    private final String system;
    private final String reportTitle;
    private final String generatedBy;
    private final String generationDate;
    private final String appliedFilters;
    private final int totalRecords;

    // inventario de flota
    private final int activeVehicles;
    private final int maintenanceVehicles;
    private final int inactiveVehicles;
    private final int vehiclesWithDriver;
    private final int vehiclesWithoutDriver;
    private final List<VehicleItem> vehicles;

    // Lista de conductores
    private final int activeDrivers;
    private final int inactiveDrivers;
    private final List<DriverItem> drivers;


    private final List<AssignmentItem> assignments;


    private final List<MaintenanceItem> maintenanceRecords;



    public record VehicleItem(
            String id, String licensePlate, String brand, String model,
            VehicleType type, Integer year, VehicleStatus status,
            LocalDate registrationDate, String assignedDriver, String notes
    ) {}

    public record DriverItem(
            String id, IdentificationType idType, String idNumber,
            String firstName, String lastName, String licenseNumber,
            LicenseCategory licenseCategory, LocalDate licenseExpiration,
            String phone, String email, DriverStatus status,
            LocalDate registrationDate, String assignedLicensePlate
    ) {}

    public record AssignmentItem(
            String vehicleId, String licensePlate,
            String driverId, String driverName,
            LocalDate startDate, LocalDate endDate,
            String status
    ) {}

    public record MaintenanceItem(
            String vehicleId, String licensePlate,
            LocalDate entryDate, LocalDate exitDate,
            String remarks
    ) {}
}
