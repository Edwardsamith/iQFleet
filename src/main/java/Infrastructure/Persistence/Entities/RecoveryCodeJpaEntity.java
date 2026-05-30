package Infrastructure.Persistence.Entities;

import Domain.Enums.RecoveryMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recovery_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryCodeJpaEntity extends BaseJpaEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_recovery_code_user"))
    private UserJpaEntity user;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false, length = 10)
    private RecoveryMethod deliveryMethod;

    @Column(name = "recipient", nullable = false, length = 150)
    private String recipient;

    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
