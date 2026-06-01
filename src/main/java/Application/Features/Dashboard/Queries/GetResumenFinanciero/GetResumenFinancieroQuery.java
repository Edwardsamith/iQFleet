package Application.Features.Dashboard.Queries.GetResumenFinanciero;

import Application.Abstractions.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GetResumenFinancieroQuery implements IQuery<ResumenFinancieroResponse> {
    private final LocalDate fechaDesde;
    private final LocalDate fechaHasta;
}
