package Application.Features.Reports.Financial;

import Application.Abstractions.IQuery;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetFinancialReportQuery implements IQuery<GetFinancialReportResponse> {


    private final String subType;

    // Common filters
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final UUID vehicleId;
    private final UUID driverId;
    private final MovementCategory category;
    private final MovementType type;
    private final PaymentMethod paymentMethod;


    private final String generatedBy;
}
