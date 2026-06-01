package Application.Features.Dashboard.Queries.GetAlertasActivas;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AlertaDocumentoItem {
    private String documentoId;
    private String nombre;
    private String tipo;
    private LocalDate fechaVencimiento;
    private long diasRestantes;
    private String estado;
    private String vehiculoPlaca;
    private String conductorNombre;
}
