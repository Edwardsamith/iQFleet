package Application.Features.Dashboard.Queries.GetResumenFinanciero;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class MovimientoRecienteItem {
    private String id;
    private String tipo;
    private BigDecimal monto;
    private LocalDate fecha;
    private String concepto;
    private String vehiculoPlaca;
}
