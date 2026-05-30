package Application.Features.Finances.Commands.Cancel;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementStatus;
import Domain.Repositories.FinancialMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CancelMovementCommandHandler
        implements IRequestHandler<CancelMovementCommand, Unit> {

    private final FinancialMovementRepository financialMovementRepository;

    @Override
    public Result<Unit> handle(CancelMovementCommand command) {

        Optional<FinancialMovement> optional = financialMovementRepository.findById(command.getId());

        if (optional.isEmpty()) {
            return Result.Failure("Movimiento financiero no encontrado");
        }

        FinancialMovement movement = optional.get();

        if (movement.getStatus() == MovementStatus.CANCELLED) {
            return Result.Failure("El movimiento ya está cancelado");
        }

        movement.setStatus(MovementStatus.CANCELLED);
        financialMovementRepository.save(movement);

        return Result.Success();
    }
}
