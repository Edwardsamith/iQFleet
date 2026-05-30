package Application.Features.Finances.Common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VehicleBalanceResponse(
        UUID vehicleId,
        String plateNumber,
        LocalDate dateFrom,
        LocalDate dateTo,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netProfit
) {}
