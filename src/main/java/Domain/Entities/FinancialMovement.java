package Domain.Entities;

import Domain.Enums.MovementCategory;
import Domain.Enums.MovementStatus;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialMovement extends Entity {
    private MovementType movementType;
    private MovementCategory category;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String notes;
    @Builder.Default
    private MovementStatus status = MovementStatus.ACTIVE;
    private UUID vehicleId;
    private UUID driverId;
    private String registeredBy;
}
