package Domain.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@jakarta.persistence.Entity
@Table(name = "assignment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentHistory extends Entity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_history_vehicle"))
    private Vehicle vehicle;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_history_driver"))
    private Driver driver;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
