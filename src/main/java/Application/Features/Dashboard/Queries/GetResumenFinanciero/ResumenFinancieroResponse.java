package Application.Features.Dashboard.Queries.GetResumenFinanciero;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ResumenFinancieroResponse {
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal balanceGeneral;
    private LocalDate periodoDesde;
    private LocalDate periodoHasta;
    private List<MovimientoRecienteItem> movimientosRecientes;
}
