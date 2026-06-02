package Application.Features.Reports.Documentary;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Document;
import Domain.Entities.DocumentVersion;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import Domain.Repositories.DocumentRepository;
import Domain.Repositories.VehicleRepository;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetDocumentaryReportQueryHandler
        implements IRequestHandler<GetDocumentaryReportQuery, GetDocumentaryReportResponse> {

    private final DocumentRepository documentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public Result<GetDocumentaryReportResponse> handle(GetDocumentaryReportQuery query) {
        return switch (query.getSubType().toUpperCase()) {
            case "FLEET_STATUS"        -> handleFleetStatus(query);
            case "DRIVERS_STATUS"      -> handleDriversStatus(query);
            case "EXPIRATIONS"         -> handleExpirations(query);
            case "EXPIRED"             -> handleExpired(query);
            case "RENEWAL_HISTORY"     -> handleRenewalHistory(query);
            default -> Result.Failure("Subtipo documental no válido: " + query.getSubType());
        };
    }

    // estado de la documentacion de la flota

    private Result<GetDocumentaryReportResponse> handleFleetStatus(GetDocumentaryReportQuery query) {
        List<Document> docs = query.getVehicleId() != null
                ? documentRepository.findByVehicleId(query.getVehicleId())
                : documentRepository.findAll().stream()
                  .filter(d -> d.getVehicleId() != null)
                  .collect(Collectors.toList());
        docs.forEach(Document::calculateStatus);
        if (query.getDocumentType() != null)
            docs = docs.stream().filter(d -> d.getDocumentType() == query.getDocumentType()).collect(Collectors.toList());
        if (query.getStatus() != null)
            docs = docs.stream().filter(d -> d.getStatus() == query.getStatus()).collect(Collectors.toList());

        Map<UUID, Vehicle> vehicleMap = vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v));
        return buildDocumentaryResponse(query, "Estado Documental de la Flota", docs, vehicleMap, Collections.emptyMap());
    }

    // estado de la documentacion de los conductores

    private Result<GetDocumentaryReportResponse> handleDriversStatus(GetDocumentaryReportQuery query) {
        List<Document> docs = query.getDriverId() != null
                ? documentRepository.findByDriverId(query.getDriverId())
                : documentRepository.findAll().stream()
                  .filter(d -> d.getDriverId() != null)
                  .collect(Collectors.toList());
        docs.forEach(Document::calculateStatus);
        if (query.getDocumentType() != null)
            docs = docs.stream().filter(d -> d.getDocumentType() == query.getDocumentType()).collect(Collectors.toList());
        if (query.getStatus() != null)
            docs = docs.stream().filter(d -> d.getStatus() == query.getStatus()).collect(Collectors.toList());

        Map<UUID, Driver> driverMap = driverRepository.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d));
        return buildDocumentaryResponse(query, "Estado Documental de Conductores", docs, Collections.emptyMap(), driverMap);
    }

    // Documentos prontos a expirar

    private Result<GetDocumentaryReportResponse> handleExpirations(GetDocumentaryReportQuery query) {
        int days = query.getDaysToExpire() != null ? query.getDaysToExpire() : 30;
        LocalDate limitDate = LocalDate.now().plusDays(days);

        List<Document> docs = documentRepository.findAll();
        docs.forEach(Document::calculateStatus);
        docs = docs.stream()
                .filter(d -> d.getExpiryDate() != null
                        && !d.getExpiryDate().isBefore(LocalDate.now())
                        && !d.getExpiryDate().isAfter(limitDate))
                .collect(Collectors.toList());
        if (query.getDocumentType() != null)
            docs = docs.stream().filter(d -> d.getDocumentType() == query.getDocumentType()).collect(Collectors.toList());

        Map<UUID, Vehicle> vehicleMap = vehicleRepository.findAll().stream().collect(Collectors.toMap(Vehicle::getId, v -> v));
        Map<UUID, Driver>  driverMap  = driverRepository.findAll().stream().collect(Collectors.toMap(Driver::getId, d -> d));
        return buildDocumentaryResponse(query, "Documentos Próximos a Vencer (" + days + " días)", docs, vehicleMap, driverMap);
    }

    // Documentos expirados

    private Result<GetDocumentaryReportResponse> handleExpired(GetDocumentaryReportQuery query) {
        List<Document> docs = documentRepository.findByStatus(DocumentStatus.EXPIRED);
        if (query.getDocumentType() != null)
            docs = docs.stream().filter(d -> d.getDocumentType() == query.getDocumentType()).collect(Collectors.toList());

        Map<UUID, Vehicle> vehicleMap = vehicleRepository.findAll().stream().collect(Collectors.toMap(Vehicle::getId, v -> v));
        Map<UUID, Driver>  driverMap  = driverRepository.findAll().stream().collect(Collectors.toMap(Driver::getId, d -> d));
        return buildDocumentaryResponse(query, "Documentos Vencidos", docs, vehicleMap, driverMap);
    }


    private Result<GetDocumentaryReportResponse> handleRenewalHistory(GetDocumentaryReportQuery query) {
        List<Document> allDocuments = documentRepository.findAll();
        if (query.getDocumentType() != null)
            allDocuments = allDocuments.stream().filter(d -> d.getDocumentType() == query.getDocumentType()).collect(Collectors.toList());

        Map<UUID, Vehicle> vehicleMap = vehicleRepository.findAll().stream().collect(Collectors.toMap(Vehicle::getId, v -> v));
        Map<UUID, Driver>  driverMap  = driverRepository.findAll().stream().collect(Collectors.toMap(Driver::getId, d -> d));

        List<GetDocumentaryReportResponse.RenewalItem> renewals = new ArrayList<>();
        for (Document doc : allDocuments) {
            if (doc.getVersions() == null || doc.getVersions().isEmpty()) continue;
            String entity = resolveEntity(doc, vehicleMap, driverMap);
            for (DocumentVersion v : doc.getVersions()) {
                renewals.add(new GetDocumentaryReportResponse.RenewalItem(
                        doc.getId().toString(),
                        doc.getName(),
                        doc.getDocumentType(),
                        entity,
                        v.getPreviousExpiryDate(),
                        v.getReplacedAt(),
                        v.getReplacedBy()
                ));
            }
        }
        renewals.sort(Comparator.comparing(
                r -> r.renewalDate() != null ? r.renewalDate() : LocalDateTime.MIN,
                Comparator.reverseOrder()));

        return Result.Success(GetDocumentaryReportResponse.builder()
                .system("iQFleet")
                .reportTitle("Historial de Renovaciones")
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .totalRecords(renewals.size())
                .active(0).expiringSoon(0).expired(0).noExpiration(0).inactive(0)
                .documents(Collections.emptyList())
                .renewals(renewals)
                .summaryByType(Collections.emptyList())
                .build());
    }

    private Result<GetDocumentaryReportResponse> buildDocumentaryResponse(
            GetDocumentaryReportQuery query, String title,
            List<Document> docs, Map<UUID, Vehicle> vehicleMap, Map<UUID, Driver> driverMap) {

        if (docs.isEmpty()) {
            return Result.Success(GetDocumentaryReportResponse.builder()
                    .system("iQFleet").reportTitle(title)
                    .generatedBy(query.getGeneratedBy())
                    .generationDate(LocalDateTime.now().format(FMT))
                    .appliedFilters(buildFilters(query))
                    .totalRecords(0)
                    .active(0).expiringSoon(0).expired(0).noExpiration(0).inactive(0)
                    .documents(Collections.emptyList())
                    .renewals(Collections.emptyList())
                    .summaryByType(Collections.emptyList())
                    .build());
        }

        List<GetDocumentaryReportResponse.DocumentItem> items = docs.stream().map(d -> {
            String entity = resolveEntity(d, vehicleMap, driverMap);
            String entityType = d.getVehicleId() != null ? "VEHICULO"
                    : d.getDriverId() != null ? "CONDUCTOR" : "ADMINISTRATIVO";
            return new GetDocumentaryReportResponse.DocumentItem(
                    d.getId().toString(), d.getName(), d.getDocumentType(),
                    d.getReferenceNumber(), d.getIssuingEntity(),
                    d.getIssueDate(), d.getExpiryDate(), d.getStatus(),
                    entity, entityType);
        }).collect(Collectors.toList());

        List<GetDocumentaryReportResponse.TypeSummaryItem> summary = Arrays.stream(DocumentType.values())
                .map(type -> {
                    List<Document> p = docs.stream().filter(d -> d.getDocumentType() == type).collect(Collectors.toList());
                    if (p.isEmpty()) return null;
                    return new GetDocumentaryReportResponse.TypeSummaryItem(type,
                            (int) p.stream().filter(d -> d.getStatus() == DocumentStatus.VALID).count(),
                            (int) p.stream().filter(d -> d.getStatus() == DocumentStatus.EXPIRING_SOON).count(),
                            (int) p.stream().filter(d -> d.getStatus() == DocumentStatus.EXPIRED).count(),
                            p.size());
                }).filter(Objects::nonNull).collect(Collectors.toList());

        return Result.Success(GetDocumentaryReportResponse.builder()
                .system("iQFleet").reportTitle(title)
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .totalRecords(docs.size())
                .active((int) docs.stream().filter(d -> d.getStatus() == DocumentStatus.VALID).count())
                .expiringSoon((int) docs.stream().filter(d -> d.getStatus() == DocumentStatus.EXPIRING_SOON).count())
                .expired((int) docs.stream().filter(d -> d.getStatus() == DocumentStatus.EXPIRED).count())
                .noExpiration((int) docs.stream().filter(d -> d.getStatus() == DocumentStatus.NO_EXPIRY).count())
                .inactive((int) docs.stream().filter(d -> d.getStatus() == DocumentStatus.INACTIVE).count())
                .documents(items)
                .renewals(Collections.emptyList())
                .summaryByType(summary)
                .build());
    }

    private String resolveEntity(Document d, Map<UUID, Vehicle> vehicleMap, Map<UUID, Driver> driverMap) {
        if (d.getVehicleId() != null && vehicleMap.containsKey(d.getVehicleId()))
            return vehicleMap.get(d.getVehicleId()).getPlateNumber();
        if (d.getDriverId() != null && driverMap.containsKey(d.getDriverId())) {
            Driver dr = driverMap.get(d.getDriverId());
            return dr.getFirstName() + " " + dr.getLastName();
        }
        return "N/A";
    }

    private String buildFilters(GetDocumentaryReportQuery q) {
        List<String> f = new ArrayList<>();
        if (q.getDocumentType()  != null) f.add("Tipo: "      + q.getDocumentType());
        if (q.getStatus()        != null) f.add("Estado: "    + q.getStatus());
        if (q.getVehicleId()     != null) f.add("Vehículo: "  + q.getVehicleId());
        if (q.getDriverId()      != null) f.add("Conductor: " + q.getDriverId());
        if (q.getDaysToExpire()  != null) f.add("Días para vencer: " + q.getDaysToExpire());
        return f.isEmpty() ? "Sin filtros adicionales" : String.join(", ", f);
    }
}
