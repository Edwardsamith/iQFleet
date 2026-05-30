package Application.Features.Finances.Common;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FleetBalanceResponse(
        LocalDate dateFrom,
        LocalDate dateTo,
        int totalVehicles,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netProfit
) {}