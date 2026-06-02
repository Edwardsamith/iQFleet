package Application.Features.Reports.Documentary;

import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetDocumentaryReportResponse {


    private final String system;
    private final String reportTitle;
    private final String generatedBy;
    private final String generationDate;
    private final String appliedFilters;
    private final int totalRecords;


    private final int active;
    private final int expiringSoon;
    private final int expired;
    private final int noExpiration;
    private final int inactive;


    private final List<DocumentItem> documents;


    private final List<RenewalItem> renewals;


    private final List<TypeSummaryItem> summaryByType;

    public record DocumentItem(
            String id,
            String name,
            DocumentType documentType,
            String referenceNumber,
            String issuingEntity,
            LocalDate issueDate,
            LocalDate expirationDate,
            DocumentStatus status,
            String associatedEntity,   // license plate or driver name
            String entityType         // VEHICLE | DRIVER | ADMINISTRATIVE
    ) {}

    public record RenewalItem(
            String documentId,
            String documentName,
            DocumentType documentType,
            String associatedEntity,
            LocalDate previousExpirationDate,
            LocalDateTime renewalDate,
            String renewedBy
    ) {}

    public record TypeSummaryItem(
            DocumentType type,
            int active,
            int expiringSoon,
            int expired,
            int total
    ) {}
}