package Application.Features.Reports.Export;

import Application.Abstractions.IQuery;
import Domain.Enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * RF-010 — Section 9: ExportReportQuery
 * Generates the exportable file in the requested format (PDF | XLSX | CSV)
 * for any of the 14 defined report types.
 */
@Getter
@AllArgsConstructor
public class ExportReportQuery implements IQuery<ExportReportResponse> {

    /** FINANCIAL | DOCUMENTARY | OPERATIONAL */
    private final String reportType;

    private final String subType;

    /** PDF | XLSX | CSV */
    private final String format;


    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final UUID vehicleId;
    private final UUID driverId;
    private final MovementType movementType;
    private final MovementCategory movementCategory;
    private final PaymentMethod paymentMethod;


    private final DocumentType documentType;
    private final DocumentStatus documentStatus;
    private final Integer daysToExpire;


    private final VehicleStatus vehicleStatus;
    private final VehicleType vehicleType;
    private final DriverStatus driverStatus;
    private final LicenseCategory licenseCategory;


    private final String generatedBy;
}