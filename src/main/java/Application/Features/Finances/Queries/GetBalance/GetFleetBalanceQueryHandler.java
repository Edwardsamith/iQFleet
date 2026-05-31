package Application.Features.Finances.Queries.GetBalance;

import Application.Abstractions.IRequestHandler;
import Application.Features.Finances.Common.FleetBalanceResponse;
import Application.Result.Result;
import Domain.Entities.Vehicle;
import Domain.Enums.MovementType;
import Domain.Repositories.FinancialMovementRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetFleetBalanceQueryHandler
        implements IRequestHandler<GetFleetBalanceQuery, FleetBalanceResponse> {

    private final FinancialMovementRepository financialMovementRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Result<FleetBalanceResponse> handle(GetFleetBalanceQuery query) {

        List<Vehicle> vehicles = vehicleRepository.findAll();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Vehicle vehicle : vehicles) {
            BigDecimal vehicleIncome = financialMovementRepository
                    .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                            vehicle.getId(), MovementType.INCOME,
                            query.getDateFrom(), query.getDateTo());

            BigDecimal vehicleExpense = financialMovementRepository
                    .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                            vehicle.getId(), MovementType.EXPENSE,
                            query.getDateFrom(), query.getDateTo());

            totalIncome = totalIncome.add(vehicleIncome);
            totalExpense = totalExpense.add(vehicleExpense);
        }

        FleetBalanceResponse response = new FleetBalanceResponse(
                query.getDateFrom(),
                query.getDateTo(),
                vehicles.size(),
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense)
        );

        return Result.Success(response);
    }
}
