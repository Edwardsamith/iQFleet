package Domain.Entities;

import Domain.Enums.RecoveryMethod;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryCode extends Entity {
    private UUID userId;
    private String codeHash;
    private RecoveryMethod deliveryMethod;
    private String recipient;
    @Builder.Default
    private String status = "PENDING";
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
}
