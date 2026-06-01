package Application.Features.Dashboard.Queries.GetAlertasActivas;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Document;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DocumentStatus;
import Domain.Repositories.DocumentRepository;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAlertasActivasQueryHandler
        implements IRequestHandler<GetAlertasActivasQuery, AlertasActivasResponse> {

    private final DocumentRepository documentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Result<AlertasActivasResponse> handle(GetAlertasActivasQuery query) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(30);

        Map<UUID, String> vehiclePlates = vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getPlateNumber));

        Map<UUID, String> driverNames = driverRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Driver::getId,
                        d -> d.getFirstName() + " " + d.getLastName()
                ));

        List<Document> alertDocs = documentRepository.findAll().stream()
                .filter(d -> d.getStatus() != DocumentStatus.INACTIVE
                        && d.getStatus() != DocumentStatus.NO_EXPIRY
                        && d.getExpiryDate() != null)
                .filter(d -> !d.getExpiryDate().isAfter(limit))
                .collect(Collectors.toList());

        List<AlertaDocumentoItem> proximos = alertDocs.stream()
                .filter(d -> !d.getExpiryDate().isBefore(today))
                .map(d -> buildDocItem(d, today, vehiclePlates, driverNames))
                .sorted((a, b) -> Long.compare(a.getDiasRestantes(), b.getDiasRestantes()))
                .collect(Collectors.toList());

        List<AlertaDocumentoItem> vencidos = alertDocs.stream()
                .filter(d -> d.getExpiryDate().isBefore(today))
                .map(d -> buildDocItem(d, today, vehiclePlates, driverNames))
                .sorted((a, b) -> Long.compare(a.getDiasRestantes(), b.getDiasRestantes()))
                .collect(Collectors.toList());

        List<AlertaConductorItem> conductoresAlerta = driverRepository.findAll().stream()
                .filter(d -> d.getLicenseExpiry() != null && !d.getLicenseExpiry().isAfter(limit))
                .map(d -> AlertaConductorItem.builder()
                        .conductorId(d.getId().toString())
                        .nombre(d.getFirstName() + " " + d.getLastName())
                        .numeroLicencia(d.getLicenseNumber())
                        .fechaVencimientoLicencia(d.getLicenseExpiry())
                        .diasRestantes(ChronoUnit.DAYS.between(today, d.getLicenseExpiry()))
                        .build())
                .sorted((a, b) -> Long.compare(a.getDiasRestantes(), b.getDiasRestantes()))
                .collect(Collectors.toList());

        return Result.Success(AlertasActivasResponse.builder()
                .documentosProximosAVencer(proximos)
                .documentosVencidos(vencidos)
                .conductoresLicenciaProxima(conductoresAlerta)
                .build());
    }

    private AlertaDocumentoItem buildDocItem(Document d, LocalDate today,
                                              Map<UUID, String> vehiclePlates,
                                              Map<UUID, String> driverNames) {
        return AlertaDocumentoItem.builder()
                .documentoId(d.getId().toString())
                .nombre(d.getName())
                .tipo(d.getDocumentType().name())
                .fechaVencimiento(d.getExpiryDate())
                .diasRestantes(ChronoUnit.DAYS.between(today, d.getExpiryDate()))
                .estado(d.getExpiryDate().isBefore(today) ? "EXPIRED" : "EXPIRING_SOON")
                .vehiculoPlaca(d.getVehicleId() != null ? vehiclePlates.getOrDefault(d.getVehicleId(), "") : "")
                .conductorNombre(d.getDriverId() != null ? driverNames.getOrDefault(d.getDriverId(), "") : "")
                .build();
    }
}
