package Infrastructure.Persistence.Mappers;

import Domain.Entities.Document;
import Infrastructure.Persistence.Entities.DocumentJpaEntity;
import Infrastructure.Persistence.Entities.DriverJpaEntity;
import Infrastructure.Persistence.Entities.VehicleJpaEntity;

public class DocumentMapper {

    public static Document toDomain(DocumentJpaEntity jpa) {
        Document domain = new Document();
        domain.setId(jpa.getId());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setName(jpa.getName());
        domain.setDocumentType(jpa.getDocumentType());
        domain.setReferenceNumber(jpa.getReferenceNumber());
        domain.setIssuingEntity(jpa.getIssuingEntity());
        domain.setIssueDate(jpa.getIssueDate());
        domain.setExpiryDate(jpa.getExpiryDate());
        domain.setStatus(jpa.getStatus());
        domain.setFileUrl(jpa.getFileUrl());
        domain.setFileFormat(jpa.getFileFormat());
        domain.setNotes(jpa.getNotes());
        domain.setUploadDate(jpa.getUploadDate());
        domain.setUploadedBy(jpa.getUploadedBy());
        if (jpa.getDriver()  != null) domain.setDriverId(jpa.getDriver().getId());
        if (jpa.getVehicle() != null) domain.setVehicleId(jpa.getVehicle().getId());
        domain.calculateStatus();
        return domain;
    }

    public static DocumentJpaEntity toJpa(Document domain,
                                           DriverJpaEntity driverRef,
                                           VehicleJpaEntity vehicleRef) {
        domain.calculateStatus();
        DocumentJpaEntity jpa = new DocumentJpaEntity();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setDocumentType(domain.getDocumentType());
        jpa.setReferenceNumber(domain.getReferenceNumber());
        jpa.setIssuingEntity(domain.getIssuingEntity());
        jpa.setIssueDate(domain.getIssueDate());
        jpa.setExpiryDate(domain.getExpiryDate());
        jpa.setStatus(domain.getStatus());
        jpa.setFileUrl(domain.getFileUrl());
        jpa.setFileFormat(domain.getFileFormat());
        jpa.setNotes(domain.getNotes());
        jpa.setUploadDate(domain.getUploadDate());
        jpa.setUploadedBy(domain.getUploadedBy());
        jpa.setDriver(driverRef);
        jpa.setVehicle(vehicleRef);
        return jpa;
    }
}
