package Application.Features.Finances.Commands.Register;

import Application.Abstractions.ICommand;
import Application.Result.Unit;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegisterMovementCommand implements ICommand<Unit> {
    private final MovementType movementType;
    private final MovementCategory category;
    private final PaymentMethod paymentMethod;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String description;
    private final String notes;
    private final UUID vehicleId;
    private final UUID driverId;
    private final String registeredBy;
}