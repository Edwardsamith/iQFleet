package Presentation.Controllers;

import Application.Abstractions.IMediator;
import Application.Features.Documents.Commands.ChangeStatus.ChangeDocumentStatusCommand;
import Application.Features.Documents.Commands.Create.CreateDocumentCommand;
import Application.Features.Documents.Commands.Renew.RenewDocumentCommand;
import Application.Features.Documents.Queries.GetAlerts.GetDocumentAlertsQuery;
import Application.Features.Documents.Queries.GetByFilter.GetDocumentsByFilterQuery;
import Application.Features.Documents.Queries.GetById.GetDocumentByIdQuery;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.Document;
import Domain.Enums.DocumentStatus;
import Domain.Enums.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final IMediator mediator;

    // POST /api/documents
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDocumentRequest request) {

        Result<Unit> result = mediator.send(
                new CreateDocumentCommand(
                        request.name(),
                        request.documentType(),
                        request.referenceNumber(),
                        request.issuingEntity(),
                        request.issueDate(),
                        request.expiryDate(),
                        request.fileUrl(),
                        request.fileFormat(),
                        request.notes(),
                        request.driverId(),
                        request.vehicleId(),
                        request.uploadedBy()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET /api/documents
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) UUID driverId,
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) DocumentType type,
            @RequestParam(required = false) DocumentStatus status) {

        Result<List<Document>> result = mediator.send(
                new GetDocumentsByFilterQuery(driverId, vehicleId, type, status)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // GET /api/documents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        Result<Document> result = mediator.send(new GetDocumentByIdQuery(id));

        if (!result.isSuccess()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getValue());
    }

    // GET /api/documents/alerts
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlerts(
            @RequestParam(required = false) Integer alertDays) {

        Result<List<Document>> result = mediator.send(
                new GetDocumentAlertsQuery(alertDays)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok(result.getValue());
    }

    // PUT /api/documents/{id}/renew
    @PutMapping("/{id}/renew")
    public ResponseEntity<?> renew(
            @PathVariable UUID id,
            @RequestBody RenewDocumentRequest request) {

        Result<Unit> result = mediator.send(
                new RenewDocumentCommand(
                        id,
                        request.newExpiryDate(),
                        request.newFileUrl(),
                        request.newFileFormat(),
                        request.replacedBy()
                )
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // PATCH /api/documents/{id}/deactivate
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable UUID id) {

        Result<Unit> result = mediator.send(
                new ChangeDocumentStatusCommand(id)
        );

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getErrors());
        }

        return ResponseEntity.ok().build();
    }

    // ─── DTOs ─────────────────────────────────────────────────────────

    record CreateDocumentRequest(
            String name,
            DocumentType documentType,
            String referenceNumber,
            String issuingEntity,
            LocalDate issueDate,
            LocalDate expiryDate,
            String fileUrl,
            String fileFormat,
            String notes,
            UUID driverId,
            UUID vehicleId,
            String uploadedBy
    ) {}

    record RenewDocumentRequest(
            LocalDate newExpiryDate,
            String newFileUrl,
            String newFileFormat,
            String replacedBy
    ) {}
}