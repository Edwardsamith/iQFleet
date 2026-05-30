package Domain.Entities;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentHistory extends Entity {
    private UUID vehicleId;
    private UUID driverId;
    private LocalDate startDate;
    private LocalDate endDate;
}
