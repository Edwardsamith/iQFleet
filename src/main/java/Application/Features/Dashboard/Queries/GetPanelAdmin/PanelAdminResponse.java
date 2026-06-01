package Application.Features.Dashboard.Queries.GetPanelAdmin;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PanelAdminResponse {
    private int totalUsuarios;
    private int usuariosActivos;
    private int usuariosInactivos;
    private int usuariosBloqueados;
    private int solicitudesPendientes;
    private List<UltimoAccesoItem> ultimosAccesos;
}
