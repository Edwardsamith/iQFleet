package Application.Features.Finances.Queries.GetByFilter;

import Application.Abstractions.IQuery;
import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetMovementsByFilterQuery implements IQuery<List<FinancialMovement>> {
    private final MovementType movementType;
    private final MovementCategory category;
    private final PaymentMethod paymentMethod;
    private final UUID vehicleId;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
}
