package Application.Features.Dashboard.Queries.GetResumenFinanciero;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.FinancialMovement;
import Domain.Entities.Vehicle;
import Domain.Enums.MovementStatus;
import Domain.Enums.MovementType;
import Domain.Repositories.FinancialMovementRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetResumenFinancieroQueryHandler
        implements IRequestHandler<GetResumenFinancieroQuery, ResumenFinancieroResponse> {

    private final FinancialMovementRepository financialMovementRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Result<ResumenFinancieroResponse> handle(GetResumenFinancieroQuery query) {
        LocalDate desde = query.getFechaDesde() != null
                ? query.getFechaDesde()
                : LocalDate.now().withDayOfMonth(1);
        LocalDate hasta = query.getFechaHasta() != null
                ? query.getFechaHasta()
                : LocalDate.now();

        Map<UUID, String> vehiclePlates = vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getPlateNumber));

        List<FinancialMovement> movements = financialMovementRepository.findAll().stream()
                .filter(m -> m.getStatus() == MovementStatus.ACTIVE)
                .filter(m -> !m.getDate().isBefore(desde) && !m.getDate().isAfter(hasta))
                .collect(Collectors.toList());

        BigDecimal totalIngresos = movements.stream()
                .filter(m -> m.getMovementType() == MovementType.INCOME)
                .map(FinancialMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEgresos = movements.stream()
                .filter(m -> m.getMovementType() == MovementType.EXPENSE)
                .map(FinancialMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MovimientoRecienteItem> recientes = movements.stream()
                .sorted(Comparator.comparing(FinancialMovement::getDate).reversed())
                .limit(5)
                .map(m -> MovimientoRecienteItem.builder()
                        .id(m.getId().toString())
                        .tipo(m.getMovementType().name())
                        .monto(m.getAmount())
                        .fecha(m.getDate())
                        .concepto(m.getDescription() != null ? m.getDescription() : m.getCategory().name())
                        .vehiculoPlaca(vehiclePlates.getOrDefault(m.getVehicleId(), ""))
                        .build())
                .collect(Collectors.toList());

        return Result.Success(ResumenFinancieroResponse.builder()
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .balanceGeneral(totalIngresos.subtract(totalEgresos))
                .periodoDesde(desde)
                .periodoHasta(hasta)
                .movimientosRecientes(recientes)
                .build());
    }
}
