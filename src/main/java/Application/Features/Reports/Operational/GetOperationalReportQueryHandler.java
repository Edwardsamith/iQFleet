package Application.Features.Reports.Operational;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.AssignmentHistory;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DriverStatus;
import Domain.Enums.VehicleStatus;
import Domain.Repositories.AssignmentHistoryRepository;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetOperationalReportQueryHandler
        implements IRequestHandler<GetOperationalReportQuery, GetOperationalReportResponse> {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final AssignmentHistoryRepository assignmentHistoryRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public Result<GetOperationalReportResponse> handle(GetOperationalReportQuery query) {
        return switch (query.getSubType().toUpperCase()) {
            case "INVENTORY"           -> handleInventory(query);
            case "DRIVERS"             -> handleDrivers(query);
            case "ASSIGNMENTS_HISTORY" -> handleAssignmentsHistory(query);
            case "MAINTENANCE_RECORDS" -> handleMaintenanceRecords(query);
            default -> Result.Failure("Subtipo operativo no válido: " + query.getSubType());
        };
    }

    // inventario de la flota

    private Result<GetOperationalReportResponse> handleInventory(GetOperationalReportQuery query) {
        List<Vehicle> vehicles = query.getVehicleStatus() != null
                ? vehicleRepository.findByStatus(query.getVehicleStatus())
                : vehicleRepository.findAll();
        if (query.getVehicleType() != null)
            vehicles = vehicles.stream().filter(v -> v.getVehicleType() == query.getVehicleType())
                    .collect(Collectors.toList());

        Map<UUID, Driver> driverMap = driverRepository.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d));

        List<GetOperationalReportResponse.VehicleItem> items = vehicles.stream().map(v -> {
            String driverName = v.getAssignedDriverId() != null && driverMap.containsKey(v.getAssignedDriverId())
                    ? driverMap.get(v.getAssignedDriverId()).getFirstName() + " " + driverMap.get(v.getAssignedDriverId()).getLastName()
                    : null;
            return new GetOperationalReportResponse.VehicleItem(
                    v.getId().toString(), v.getPlateNumber(), v.getBrand(), v.getVehicleModel(),
                    v.getVehicleType(), v.getYear(), v.getStatus(), v.getRegistrationDate(), driverName, v.getNotes());
        }).collect(Collectors.toList());

        int vehiclesWithDriver = (int) vehicles.stream().filter(v -> v.getAssignedDriverId() != null).count();

        return Result.Success(GetOperationalReportResponse.builder()
                .system("iQFleet").reportTitle("Inventario de Flota")
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .totalRecords(vehicles.size())
                .activeVehicles((int) vehicles.stream().filter(v -> v.getStatus() == VehicleStatus.ACTIVE).count())
                .maintenanceVehicles((int) vehicles.stream().filter(v -> v.getStatus() == VehicleStatus.UNDER_MAINTENANCE).count())
                .inactiveVehicles((int) vehicles.stream().filter(v -> v.getStatus() == VehicleStatus.INACTIVE).count())
                .vehiclesWithDriver(vehiclesWithDriver)
                .vehiclesWithoutDriver(vehicles.size() - vehiclesWithDriver)
                .vehicles(items)
                .drivers(Collections.emptyList())
                .assignments(Collections.emptyList())
                .maintenanceRecords(Collections.emptyList())
                .build());
    }

    // Lista de conductores

    private Result<GetOperationalReportResponse> handleDrivers(GetOperationalReportQuery query) {
        List<Driver> drivers = query.getDriverStatus() != null
                ? driverRepository.findByStatus(query.getDriverStatus())
                : driverRepository.findAll();
        if (query.getDriverCategory() != null)
            drivers = drivers.stream()
                    .filter(d -> d.getLicenseCategory() == query.getDriverCategory())
                    .collect(Collectors.toList());

        Map<UUID, Vehicle> vehicleByDriver = vehicleRepository.findAll().stream()
                .filter(v -> v.getAssignedDriverId() != null)
                .collect(Collectors.toMap(Vehicle::getAssignedDriverId, v -> v, (a, b) -> a));

        List<GetOperationalReportResponse.DriverItem> items = drivers.stream().map(d -> {
            String plateNumber = vehicleByDriver.containsKey(d.getId())
                    ? vehicleByDriver.get(d.getId()).getPlateNumber() : null;
            return new GetOperationalReportResponse.DriverItem(
                    d.getId().toString(), d.getIdentificationType(), d.getIdentificationNumber(),
                    d.getFirstName(), d.getLastName(), d.getLicenseNumber(), d.getLicenseCategory(),
                    d.getLicenseExpiry(), d.getPhone(), d.getEmail(), d.getStatus(),
                    d.getRegistrationDate(), plateNumber);
        }).collect(Collectors.toList());

        return Result.Success(GetOperationalReportResponse.builder()
                .system("iQFleet").reportTitle("Listado de Conductores")
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .totalRecords(drivers.size())
                .activeVehicles(0).maintenanceVehicles(0).inactiveVehicles(0)
                .vehiclesWithDriver(0).vehiclesWithoutDriver(0)
                .vehicles(Collections.emptyList())
                .activeDrivers((int) drivers.stream().filter(d -> d.getStatus() == DriverStatus.ACTIVE).count())
                .inactiveDrivers((int) drivers.stream().filter(d -> d.getStatus() == DriverStatus.INACTIVE).count())
                .drivers(items)
                .assignments(Collections.emptyList())
                .maintenanceRecords(Collections.emptyList())
                .build());
    }



    private Result<GetOperationalReportResponse> handleAssignmentsHistory(GetOperationalReportQuery query) {
        List<AssignmentHistory> history;
        if (query.getVehicleId() != null)
            history = assignmentHistoryRepository.findByVehicleId(query.getVehicleId());
        else if (query.getDriverId() != null)
            history = assignmentHistoryRepository.findByDriverId(query.getDriverId());
        else
            history = assignmentHistoryRepository.findAll();


        if (query.getFromDate() != null)
            history = history.stream()
                    .filter(a -> a.getStartDate() != null && !a.getStartDate().isBefore(query.getFromDate()))
                    .collect(Collectors.toList());
        if (query.getToDate() != null)
            history = history.stream()
                    .filter(a -> a.getStartDate() != null && !a.getStartDate().isAfter(query.getToDate()))
                    .collect(Collectors.toList());

        Map<UUID, Vehicle> vehicleMap = vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v));
        Map<UUID, Driver> driverMap = driverRepository.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d));

        List<GetOperationalReportResponse.AssignmentItem> items = history.stream().map(a -> {
                    String licensePlate = vehicleMap.containsKey(a.getVehicleId()) ? vehicleMap.get(a.getVehicleId()).getPlateNumber() : "N/A";
                    String driverName = driverMap.containsKey(a.getDriverId())
                            ? driverMap.get(a.getDriverId()).getFirstName() + " " + driverMap.get(a.getDriverId()).getLastName()
                            : "N/A";
                    String status = a.getEndDate() == null ? "ACTIVA" : "FINALIZADA";
                    return new GetOperationalReportResponse.AssignmentItem(
                            a.getVehicleId().toString(), licensePlate,
                            a.getDriverId().toString(), driverName,
                            a.getStartDate(), a.getEndDate(), status);
                }).sorted(Comparator.comparing(GetOperationalReportResponse.AssignmentItem::startDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return Result.Success(GetOperationalReportResponse.builder()
                .system("iQFleet").reportTitle("Historial de Asignaciones")
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .totalRecords(items.size())
                .activeVehicles(0).maintenanceVehicles(0).inactiveVehicles(0)
                .vehiclesWithDriver(0).vehiclesWithoutDriver(0)
                .vehicles(Collections.emptyList())
                .activeDrivers(0).inactiveDrivers(0)
                .drivers(Collections.emptyList())
                .assignments(items)
                .maintenanceRecords(Collections.emptyList())
                .build());
    }



    private Result<GetOperationalReportResponse> handleMaintenanceRecords(GetOperationalReportQuery query) {
        List<Vehicle> vehicles = query.getVehicleId() != null
                ? vehicleRepository.findById(query.getVehicleId()).map(List::of).orElse(Collections.emptyList())
                : vehicleRepository.findAll();

        List<Vehicle> underMaintenance = vehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.UNDER_MAINTENANCE)
                .collect(Collectors.toList());

        List<GetOperationalReportResponse.MaintenanceItem> items = underMaintenance.stream()
                .map(v -> new GetOperationalReportResponse.MaintenanceItem(
                        v.getId().toString(),
                        v.getPlateNumber(),
                        v.getRegistrationDate(),  // referential date
                        null,
                        v.getNotes()))
                .collect(Collectors.toList());

        return Result.Success(GetOperationalReportResponse.builder()
                .system("iQFleet").reportTitle("Registro de Mantenimientos")
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .totalRecords(items.size())
                .activeVehicles(0).maintenanceVehicles(items.size()).inactiveVehicles(0)
                .vehiclesWithDriver(0).vehiclesWithoutDriver(0)
                .vehicles(Collections.emptyList())
                .activeDrivers(0).inactiveDrivers(0)
                .drivers(Collections.emptyList())
                .assignments(Collections.emptyList())
                .maintenanceRecords(items)
                .build());
    }



    private String buildFilters(GetOperationalReportQuery q) {
        List<String> filters = new ArrayList<>();
        if (q.getVehicleStatus()  != null) filters.add("Estado vehículo: "   + q.getVehicleStatus());
        if (q.getVehicleType()    != null) filters.add("Tipo vehículo: "     + q.getVehicleType());
        if (q.getDriverStatus()   != null) filters.add("Estado conductor: "  + q.getDriverStatus());
        if (q.getDriverCategory() != null) filters.add("Categoría licencia: " + q.getDriverCategory());
        if (q.getVehicleId()      != null) filters.add("Vehículo: "          + q.getVehicleId());
        if (q.getDriverId()       != null) filters.add("Conductor: "         + q.getDriverId());
        if (q.getFromDate()       != null) filters.add("Desde: "             + q.getFromDate());
        if (q.getToDate()         != null) filters.add("Hasta: "             + q.getToDate());
        return filters.isEmpty() ? "Sin filtros adicionales" : String.join(", ", filters);
    }
}
