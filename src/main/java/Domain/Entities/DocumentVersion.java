package Domain.Entities;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVersion extends Entity {
    private UUID documentId;
    private String fileUrl;
    private LocalDate previousExpiryDate;
    private String replacedBy;
    private LocalDateTime replacedAt;
}
