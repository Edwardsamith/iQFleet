package Application.Features.Dashboard.Queries.GetPanelAdmin;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UltimoAccesoItem {
    private String usuarioId;
    private String nombre;
    private String rol;
    private LocalDateTime ultimoAcceso;
}
