package Application.Features.Reports.Financial;

import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class GetFinancialReportResponse {


    private final String system;            // "iQFleet"
    private final String reportTitle;
    private final String generatedBy;
    private final String generationDate;    // dd/MM/yyyy HH:mm
    private final String appliedFilters;
    private final String period;            // "fromDate – toDate"
    private final int totalRecords;


    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;
    private final BigDecimal balance;


    private final VehicleBalanceData vehicleBalance;


    private final List<VehicleBalanceItem> fleetConsolidated;


    private final List<MovementItem> movements;


    private final List<ProfitabilityItem> profitability;


    private final List<ExpensesByCategoryItem> expensesByCategory;



    public record VehicleBalanceData(
            String vehicleId,
            String licensePlate,
            String brand,
            String model,
            LocalDate periodFrom,
            LocalDate periodTo,
            BigDecimal income,
            BigDecimal expenses,
            BigDecimal netProfit
    ) {}

    public record VehicleBalanceItem(
            String vehicleId,
            String licensePlate,
            BigDecimal income,
            BigDecimal expenses,
            BigDecimal netProfit
    ) {}

    public record MovementItem(
            String id,
            MovementType type,
            MovementCategory category,
            PaymentMethod paymentMethod,
            BigDecimal amount,
            LocalDate date,
            String description,
            String notes,
            String status,
            String licensePlate,
            String driver
    ) {}

    public record ProfitabilityItem(
            String vehicleId,
            String licensePlate,
            BigDecimal income,
            BigDecimal expenses,
            BigDecimal netProfit,
            double marginPercentage
    ) {}

    public record ExpensesByCategoryItem(
            MovementCategory category,
            BigDecimal total,
            int count,
            double percentageOfTotal
    ) {}
}