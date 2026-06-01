package Application.Features.Dashboard.Queries.GetAlertasActivas;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlertasActivasResponse {
    private List<AlertaDocumentoItem> documentosProximosAVencer;
    private List<AlertaDocumentoItem> documentosVencidos;
    private List<AlertaConductorItem> conductoresLicenciaProxima;
}
