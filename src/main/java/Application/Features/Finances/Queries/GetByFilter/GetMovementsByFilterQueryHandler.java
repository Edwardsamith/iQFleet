package Application.Features.Finances.Queries.GetByFilter;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.FinancialMovement;
import Domain.Repositories.FinancialMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetMovementsByFilterQueryHandler
        implements IRequestHandler<GetMovementsByFilterQuery, List<FinancialMovement>> {

    private final FinancialMovementRepository financialMovementRepository;

    @Override
    public Result<List<FinancialMovement>> handle(GetMovementsByFilterQuery query) {

        List<FinancialMovement> movements = financialMovementRepository.findAll();

        if (query.getMovementType() != null) {
            movements = movements.stream()
                    .filter(m -> m.getMovementType() == query.getMovementType())
                    .collect(Collectors.toList());
        }

        if (query.getCategory() != null) {
            movements = movements.stream()
                    .filter(m -> m.getCategory() == query.getCategory())
                    .collect(Collectors.toList());
        }

        if (query.getPaymentMethod() != null) {
            movements = movements.stream()
                    .filter(m -> m.getPaymentMethod() == query.getPaymentMethod())
                    .collect(Collectors.toList());
        }

        if (query.getVehicleId() != null) {
            movements = movements.stream()
                    .filter(m -> query.getVehicleId().equals(m.getVehicleId()))
                    .collect(Collectors.toList());
        }

        if (query.getDateFrom() != null) {
            movements = movements.stream()
                    .filter(m -> !m.getDate().isBefore(query.getDateFrom()))
                    .collect(Collectors.toList());
        }

        if (query.getDateTo() != null) {
            movements = movements.stream()
                    .filter(m -> !m.getDate().isAfter(query.getDateTo()))
                    .collect(Collectors.toList());
        }

        return Result.Success(movements);
    }
}
