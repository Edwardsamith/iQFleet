package Application.Features.Dashboard.Queries.GetResumenFlota;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Enums.DriverStatus;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.DocumentRepository;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetResumenFlotaQueryHandler
        implements IRequestHandler<GetResumenFlotaQuery, ResumenFlotaResponse> {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final DocumentRepository documentRepository;

    @Override
    public Result<ResumenFlotaResponse> handle(GetResumenFlotaQuery query) {
        int totalVehiculos     = vehicleRepository.findAll().size();
        int vehiculosActivos   = vehicleRepository.findByStatus(VehicleStatus.ACTIVE).size();
        int enMantenimiento    = vehicleRepository.findByStatus(VehicleStatus.UNDER_MAINTENANCE).size();
        int vehiculosInactivos = vehicleRepository.findByStatus(VehicleStatus.INACTIVE).size();

        int totalConductores     = driverRepository.findAll().size();
        int conductoresActivos   = driverRepository.findByStatus(DriverStatus.ACTIVE).size();
        int conductoresInactivos = driverRepository.findByStatus(DriverStatus.INACTIVE).size();

        int totalDocumentos = documentRepository.findAll().size();

        return Result.Success(ResumenFlotaResponse.builder()
                .totalVehiculos(totalVehiculos)
                .vehiculosActivos(vehiculosActivos)
                .vehiculosEnMantenimiento(enMantenimiento)
                .vehiculosInactivos(vehiculosInactivos)
                .totalConductores(totalConductores)
                .conductoresActivos(conductoresActivos)
                .conductoresInactivos(conductoresInactivos)
                .totalDocumentos(totalDocumentos)
                .build());
    }
}
