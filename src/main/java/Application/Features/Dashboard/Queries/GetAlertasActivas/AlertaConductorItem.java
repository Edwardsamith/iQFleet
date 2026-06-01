package Application.Features.Dashboard.Queries.GetAlertasActivas;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AlertaConductorItem {
    private String conductorId;
    private String nombre;
    private String numeroLicencia;
    private LocalDate fechaVencimientoLicencia;
    private long diasRestantes;
}
