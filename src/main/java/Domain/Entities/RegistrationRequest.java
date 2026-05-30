package Domain.Entities;

import Domain.Enums.DecisionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest extends Entity {
    private UUID userId;
    private DecisionStatus decision;
    private String rejectionReason;
    private UUID reviewedById;
    private LocalDateTime decisionDate;
}
