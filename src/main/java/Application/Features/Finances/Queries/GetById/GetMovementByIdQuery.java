package Application.Features.Finances.Queries.GetById;

import Application.Abstractions.IQuery;
import Domain.Entities.FinancialMovement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetMovementByIdQuery implements IQuery<FinancialMovement> {
    private final UUID id;
}