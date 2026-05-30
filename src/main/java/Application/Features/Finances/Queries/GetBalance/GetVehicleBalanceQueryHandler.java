package Application.Features.Finances.Queries.GetBalance;

import Application.Abstractions.IRequestHandler;
import Application.Features.Finances.Common.VehicleBalanceResponse;
import Application.Result.Result;
import Domain.Entities.Vehicle;
import Domain.Enums.MovementType;
import Domain.Repositories.FinancialMovementRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetVehicleBalanceQueryHandler
        implements IRequestHandler<GetVehicleBalanceQuery, VehicleBalanceResponse> {

    private final FinancialMovementRepository financialMovementRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public Result<VehicleBalanceResponse> handle(GetVehicleBalanceQuery query) {

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(query.getVehicleId());
        if (vehicleOpt.isEmpty()) {
            return Result.Failure("El vehiculo especificado no existe");
        }

        Vehicle vehicle = vehicleOpt.get();

        BigDecimal totalIncome = financialMovementRepository
                .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                        query.getVehicleId(), MovementType.INCOME,
                        query.getDateFrom(), query.getDateTo());

        BigDecimal totalExpense = financialMovementRepository
                .sumAmountByVehicleIdAndMovementTypeAndDateBetween(
                        query.getVehicleId(), MovementType.EXPENSE,
                        query.getDateFrom(), query.getDateTo());

        VehicleBalanceResponse response = new VehicleBalanceResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                query.getDateFrom(),
                query.getDateTo(),
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense)
        );

        return Result.Success(response);
    }
}
