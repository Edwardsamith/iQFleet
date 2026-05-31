package Domain.Entities;

import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends Entity {
    private String name;
    private DocumentType documentType;
    private String referenceNumber;
    private String issuingEntity;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    @Builder.Default
    private DocumentStatus status = DocumentStatus.VALID;
    private String fileUrl;
    private String fileFormat;
    private String notes;
    private LocalDateTime uploadDate;
    private String uploadedBy;
    private UUID driverId;
    private UUID vehicleId;
    @Builder.Default
    private List<DocumentVersion> versions = new ArrayList<>();

    public void calculateStatus() {
        if (expiryDate == null) {
            this.status = DocumentStatus.NO_EXPIRY;
            return;
        }
        if (this.status == DocumentStatus.INACTIVE) {
            return;
        }
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            this.status = DocumentStatus.EXPIRED;
        } else if (!expiryDate.isAfter(today.plusDays(30))) {
            this.status = DocumentStatus.EXPIRING_SOON;
        } else {
            this.status = DocumentStatus.VALID;
        }
    }
}
