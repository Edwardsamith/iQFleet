package Application.Features.Finances.Queries.GetById;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Domain.Entities.FinancialMovement;
import Domain.Repositories.FinancialMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetMovementByIdQueryHandler
        implements IRequestHandler<GetMovementByIdQuery, FinancialMovement> {

    private final FinancialMovementRepository financialMovementRepository;

    @Override
    public Result<FinancialMovement> handle(GetMovementByIdQuery query) {
        return financialMovementRepository.findById(query.getId())
                .map(Result::Success)
                .orElse(Result.Failure("Movimiento financiero no encontrado"));
    }
}
