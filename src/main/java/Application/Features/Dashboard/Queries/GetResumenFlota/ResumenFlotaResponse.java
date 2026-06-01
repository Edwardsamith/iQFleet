package Application.Features.Dashboard.Queries.GetResumenFlota;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumenFlotaResponse {
    private int totalVehiculos;
    private int vehiculosActivos;
    private int vehiculosEnMantenimiento;
    private int vehiculosInactivos;
    private int totalConductores;
    private int conductoresActivos;
    private int conductoresInactivos;
    private int totalDocumentos;
}
