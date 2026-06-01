package Application.Features.Reports.Financial;

import java.math.BigDecimal;

public record GetFinancialReportResponse(
        BigDecimal ingresos,
        BigDecimal egresos,
        BigDecimal balance
) {
}
