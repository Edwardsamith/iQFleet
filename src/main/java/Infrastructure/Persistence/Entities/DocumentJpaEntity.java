package Infrastructure.Persistence.Entities;

import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentJpaEntity extends BaseJpaEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 30)
    private DocumentType documentType;

    @Column(name = "reference_number", length = 50)
    private String referenceNumber;

    @Column(name = "issuing_entity", length = 100)
    private String issuingEntity;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.VALID;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_format", length = 10)
    private String fileFormat;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", foreignKey = @ForeignKey(name = "fk_document_driver"))
    private DriverJpaEntity driver;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", foreignKey = @ForeignKey(name = "fk_document_vehicle"))
    private VehicleJpaEntity vehicle;

    @ToString.Exclude
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentVersionJpaEntity> versions = new ArrayList<>();
}
