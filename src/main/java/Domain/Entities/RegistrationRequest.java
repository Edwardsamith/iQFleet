package Domain.Entities;

import Domain.Enums.DecisionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(
    name = "registration_requests",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_registration_request_user", columnNames = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest extends Entity {

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_registration_request_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", length = 15)
    private DecisionStatus decision;

    @Column(name = "rejection_reason", length = 300)
    private String rejectionReason;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", foreignKey = @ForeignKey(name = "fk_registration_request_reviewed_by"))
    private User reviewedBy;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;
}
