package Domain.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "document_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVersion extends Entity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "fk_document_version_document"))
    private Document document;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "previous_expiry_date")
    private LocalDate previousExpiryDate;

    @Column(name = "replaced_by", length = 100)
    private String replacedBy;

    @Column(name = "replaced_at", nullable = false)
    private LocalDateTime replacedAt;
}
