package Application.Features.Reports.Financial;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.Driver;
import Domain.Entities.FinancialMovement;
import Domain.Entities.Vehicle;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementStatus;
import Domain.Enums.MovementType;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.FinancialMovementRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetFinancialReportQueryHandler
        implements IRequestHandler<GetFinancialReportQuery, GetFinancialReportResponse> {

    private final FinancialMovementRepository financialMovementRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public Result<GetFinancialReportResponse> handle(GetFinancialReportQuery query) {


        if (query.getFromDate() != null && query.getToDate() != null) {
            if (query.getFromDate().isAfter(query.getToDate()))
                return Result.Failure("La fecha de inicio no puede ser posterior a la fecha de fin");
            if (query.getFromDate().plusMonths(12).isBefore(query.getToDate()))
                return Result.Failure("El rango de fechas no puede superar los 12 meses");
        }

        return switch (query.getSubType().toUpperCase()) {
            case "VEHICLE_BALANCE"    -> handleVehicleBalance(query);
            case "CONSOLIDATED"       -> handleConsolidated(query);
            case "MOVEMENTS"          -> handleMovements(query);
            case "PROFITABILITY"      -> handleProfitability(query);
            case "EXPENSES_CATEGORY"  -> handleExpensesCategory(query);
            default -> Result.Failure("Subtipo financiero no válido: " + query.getSubType());
        };
    }



    private Result<GetFinancialReportResponse> handleVehicleBalance(GetFinancialReportQuery query) {
        if (query.getVehicleId() == null)
            return Result.Failure("vehiculoId es requerido para el reporte de balance por vehículo");

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(query.getVehicleId());
        if (vehicleOpt.isEmpty()) return Result.Failure("El vehículo especificado no existe");
        Vehicle v = vehicleOpt.get();

        BigDecimal income = financialMovementRepository
                .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                        query.getVehicleId(), MovementType.INCOME,
                        query.getFromDate(), query.getToDate());
        BigDecimal expenses = financialMovementRepository
                .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                        query.getVehicleId(), MovementType.EXPENSE,
                        query.getFromDate(), query.getToDate());

        GetFinancialReportResponse.VehicleBalanceData data =
                new GetFinancialReportResponse.VehicleBalanceData(
                        v.getId().toString(), v.getPlateNumber(), v.getBrand(), v.getVehicleModel(),
                        query.getFromDate(), query.getToDate(),
                        income, expenses, income.subtract(expenses));

        return Result.Success(buildResponse(query, "Balance por Vehículo", 1,
                income, expenses,
                b -> b.vehicleBalance(data)));
    }



    private Result<GetFinancialReportResponse> handleConsolidated(GetFinancialReportQuery query) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<GetFinancialReportResponse.VehicleBalanceItem> items = new ArrayList<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Vehicle v : vehicles) {
            BigDecimal inc = financialMovementRepository
                    .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                            v.getId(), MovementType.INCOME, query.getFromDate(), query.getToDate());
            BigDecimal exp = financialMovementRepository
                    .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                            v.getId(), MovementType.EXPENSE, query.getFromDate(), query.getToDate());
            totalIncome = totalIncome.add(inc);
            totalExpenses = totalExpenses.add(exp);
            items.add(new GetFinancialReportResponse.VehicleBalanceItem(
                    v.getId().toString(), v.getPlateNumber(), inc, exp, inc.subtract(exp)));
        }

        final BigDecimal ti = totalIncome, te = totalExpenses;
        return Result.Success(buildResponse(query, "Balance Consolidado de Flota", vehicles.size(),
                ti, te, b -> b.fleetConsolidated(items)));
    }



    private Result<GetFinancialReportResponse> handleMovements(GetFinancialReportQuery query) {
        List<FinancialMovement> allMovements = getFilteredMovements(query);

        if (allMovements.isEmpty())
            return Result.Success(buildEmptyResponse(query, "Movimientos Financieros"));

        Map<UUID, Vehicle> vehicleMap = vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v));
        Map<UUID, Driver> driverMap = driverRepository.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d));

        List<GetFinancialReportResponse.MovementItem> items = allMovements.stream().map(m -> {
            String licensePlate = m.getVehicleId() != null && vehicleMap.containsKey(m.getVehicleId())
                    ? vehicleMap.get(m.getVehicleId()).getPlateNumber() : "N/A";
            String driverName = m.getDriverId() != null && driverMap.containsKey(m.getDriverId())
                    ? driverMap.get(m.getDriverId()).getFirstName() + " " + driverMap.get(m.getDriverId()).getLastName()
                    : "N/A";
            return new GetFinancialReportResponse.MovementItem(
                    m.getId().toString(), m.getMovementType(), m.getCategory(),
                    m.getPaymentMethod(), m.getAmount(), m.getDate(),
                    m.getDescription(), m.getNotes(), m.getStatus().name(), licensePlate, driverName);
        }).collect(Collectors.toList());

        BigDecimal ti = allMovements.stream().filter(m -> m.getMovementType() == MovementType.INCOME)
                .map(FinancialMovement::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal te = allMovements.stream().filter(m -> m.getMovementType() == MovementType.EXPENSE)
                .map(FinancialMovement::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return Result.Success(buildResponse(query, "Movimientos Financieros", allMovements.size(),
                ti, te, b -> b.movements(items)));
    }



    private Result<GetFinancialReportResponse> handleProfitability(GetFinancialReportQuery query) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<GetFinancialReportResponse.ProfitabilityItem> items = new ArrayList<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Vehicle v : vehicles) {
            BigDecimal inc = financialMovementRepository
                    .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                            v.getId(), MovementType.INCOME, query.getFromDate(), query.getToDate());
            BigDecimal exp = financialMovementRepository
                    .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                            v.getId(), MovementType.EXPENSE, query.getFromDate(), query.getToDate());
            BigDecimal netProfit = inc.subtract(exp);
            double margin = inc.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                    : netProfit.divide(inc, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();

            totalIncome = totalIncome.add(inc);
            totalExpenses = totalExpenses.add(exp);
            items.add(new GetFinancialReportResponse.ProfitabilityItem(
                    v.getId().toString(), v.getPlateNumber(), inc, exp, netProfit, margin));
        }


        items.sort(Comparator.comparing(GetFinancialReportResponse.ProfitabilityItem::netProfit).reversed());

        final BigDecimal ti = totalIncome, te = totalExpenses;
        return Result.Success(buildResponse(query, "Rentabilidad Comparativa", vehicles.size(),
                ti, te, b -> b.profitability(items)));
    }



    private Result<GetFinancialReportResponse> handleExpensesCategory(GetFinancialReportQuery query) {
        List<FinancialMovement> expenses = getFilteredMovements(query).stream()
                .filter(m -> m.getMovementType() == MovementType.EXPENSE)
                .collect(Collectors.toList());

        if (expenses.isEmpty())
            return Result.Success(buildEmptyResponse(query, "Egresos por Categoría"));

        BigDecimal totalExpenses = expenses.stream()
                .map(FinancialMovement::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<GetFinancialReportResponse.ExpensesByCategoryItem> items =
                Arrays.stream(MovementCategory.values())
                        .map(cat -> {
                            List<FinancialMovement> byCat = expenses.stream()
                                    .filter(m -> m.getCategory() == cat).collect(Collectors.toList());
                            if (byCat.isEmpty()) return null;
                            BigDecimal total = byCat.stream()
                                    .map(FinancialMovement::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                            double pct = totalExpenses.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                                    : total.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                                      .multiply(BigDecimal.valueOf(100)).doubleValue();
                            return new GetFinancialReportResponse.ExpensesByCategoryItem(cat, total, byCat.size(), pct);
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(GetFinancialReportResponse.ExpensesByCategoryItem::total).reversed())
                        .collect(Collectors.toList());

        return Result.Success(buildResponse(query, "Egresos por Categoría", expenses.size(),
                BigDecimal.ZERO, totalExpenses, b -> b.expensesByCategory(items)));
    }



    private List<FinancialMovement> getFilteredMovements(GetFinancialReportQuery query) {
        List<FinancialMovement> list;
        if (query.getVehicleId() != null) {
            list = (query.getFromDate() != null && query.getToDate() != null)
                    ? financialMovementRepository.findByVehicleIdAndDateBetween(
                    query.getVehicleId(), query.getFromDate(), query.getToDate())
                    : financialMovementRepository.findByVehicleId(query.getVehicleId());
        } else {
            list = financialMovementRepository.findAll();
            if (query.getFromDate() != null && query.getToDate() != null)
                list = list.stream()
                        .filter(m -> !m.getDate().isBefore(query.getFromDate())
                                && !m.getDate().isAfter(query.getToDate()))
                        .collect(Collectors.toList());
        }

        if (query.getDriverId() != null)
            list = list.stream().filter(m -> query.getDriverId().equals(m.getDriverId())).collect(Collectors.toList());
        if (query.getType() != null)
            list = list.stream().filter(m -> m.getMovementType() == query.getType()).collect(Collectors.toList());
        if (query.getCategory() != null)
            list = list.stream().filter(m -> m.getCategory() == query.getCategory()).collect(Collectors.toList());
        if (query.getPaymentMethod() != null)
            list = list.stream().filter(m -> m.getPaymentMethod() == query.getPaymentMethod()).collect(Collectors.toList());
        // Active movements only
        list = list.stream().filter(m -> m.getStatus() == MovementStatus.ACTIVE).collect(Collectors.toList());
        return list;
    }

    @FunctionalInterface
    private interface BuilderCustomizer {
        GetFinancialReportResponse.GetFinancialReportResponseBuilder apply(
                GetFinancialReportResponse.GetFinancialReportResponseBuilder b);
    }

    private GetFinancialReportResponse buildResponse(GetFinancialReportQuery query, String title,
                                                  int totalRecords, BigDecimal totalIncome, BigDecimal totalExpenses,
                                                  BuilderCustomizer customizer) {

        GetFinancialReportResponse.GetFinancialReportResponseBuilder b =
                GetFinancialReportResponse.builder()
                        .system("iQFleet")
                        .reportTitle(title)
                        .generatedBy(query.getGeneratedBy())
                        .generationDate(LocalDateTime.now().format(FMT))
                        .appliedFilters(buildFilters(query))
                        .period(buildPeriod(query))
                        .totalRecords(totalRecords)
                        .totalIncome(totalIncome)
                        .totalExpenses(totalExpenses)
                        .balance(totalIncome.subtract(totalExpenses));
        return customizer.apply(b).build();
    }

    private GetFinancialReportResponse buildEmptyResponse(GetFinancialReportQuery query, String title) {
        return GetFinancialReportResponse.builder()
                .system("iQFleet")
                .reportTitle(title)
                .generatedBy(query.getGeneratedBy())
                .generationDate(LocalDateTime.now().format(FMT))
                .appliedFilters(buildFilters(query))
                .period(buildPeriod(query))
                .totalRecords(0)
                .totalIncome(BigDecimal.ZERO)
                .totalExpenses(BigDecimal.ZERO)
                .balance(BigDecimal.ZERO)
                .build();
    }

    private String buildPeriod(GetFinancialReportQuery q) {
        String from = q.getFromDate() != null ? q.getFromDate().toString() : "—";
        String to = q.getToDate() != null ? q.getToDate().toString() : "—";
        return from + " – " + to;
    }

    private String buildFilters(GetFinancialReportQuery q) {
        List<String> f = new ArrayList<>();
        if (q.getFromDate()      != null) f.add("Desde: "      + q.getFromDate());
        if (q.getToDate()        != null) f.add("Hasta: "      + q.getToDate());
        if (q.getVehicleId()     != null) f.add("Vehículo: "   + q.getVehicleId());
        if (q.getDriverId()      != null) f.add("Conductor: "  + q.getDriverId());
        if (q.getType()          != null) f.add("Tipo: "       + q.getType());
        if (q.getCategory()      != null) f.add("Categoría: "  + q.getCategory());
        if (q.getPaymentMethod() != null) f.add("Método pago: " + q.getPaymentMethod());
        return f.isEmpty() ? "Sin filtros adicionales" : String.join(", ", f);
    }
}
