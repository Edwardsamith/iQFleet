package Application.Features.Reports.Documentary;

import Application.Abstractions.IQuery;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetDocumentaryReportQuery implements IQuery<GetDocumentaryReportResponse> {


    private final String subType;

    private final DocumentType documentType;
    private final DocumentStatus status;
    private final UUID vehicleId;
    private final UUID driverId;
    private final Integer daysToExpire;    // for EXPIRATIONS subtype
    private final String generatedBy;
}