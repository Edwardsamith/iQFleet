package Infrastructure.Seeder;

import Domain.Entities.Document;
import Domain.Entities.Driver;
import Domain.Entities.Vehicle;
import Domain.Enums.DocumentType;
import Infrastructure.Repositories.JpaDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentSeeder {

    private final JpaDocumentRepository documentRepository;

    /**
     * @param drivers  [0]=Carlos, [1]=Luis, [2]=María, [3]=Jorge, [4]=Ana
     * @param vehicles [0]=bus1, [1]=microbus, [2]=van, [3]=bus2, [4]=minibus
     */
    public void seed(List<Driver> drivers, List<Vehicle> vehicles) {
        seedVehicleDocuments(vehicles);
        seedDriverDocuments(drivers);
        log.info("Documentos de vehículos y conductores creados.");
    }

    private void seedVehicleDocuments(List<Vehicle> vehicles) {
        Vehicle bus1     = vehicles.get(0);
        Vehicle microbus = vehicles.get(1);
        Vehicle van      = vehicles.get(2);
        Vehicle bus2     = vehicles.get(3);
        Vehicle minibus  = vehicles.get(4);

        LocalDateTime now = LocalDateTime.now();

        // --- ABC-123 (Mercedes-Benz Sprinter) ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("SOAT ABC-123")
                        .documentType(DocumentType.SOAT)
                        .referenceNumber("SOAT-2024-001234")
                        .issuingEntity("Seguros Bolívar")
                        .issueDate(LocalDate.of(2024, 1, 1))
                        .expiryDate(LocalDate.of(2025, 12, 31))
                        .fileUrl("https://storage.iqfleet.co/docs/soat-abc123-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(4))
                        .uploadedBy("fabian.owner")
                        .vehicle(bus1)
                        .build(),

                Document.builder()
                        .name("RTM ABC-123")
                        .documentType(DocumentType.RTM)
                        .referenceNumber("RTM-2024-005678")
                        .issuingEntity("Centro de Diagnóstico Automotor")
                        .issueDate(LocalDate.of(2024, 2, 15))
                        .expiryDate(LocalDate.of(2026, 2, 14))
                        .fileUrl("https://storage.iqfleet.co/docs/rtm-abc123-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(3))
                        .uploadedBy("andres.admin")
                        .vehicle(bus1)
                        .build(),

                Document.builder()
                        .name("Tarjeta de Operación ABC-123")
                        .documentType(DocumentType.OPERATION_CARD)
                        .referenceNumber("TO-2024-ABC123")
                        .issuingEntity("Secretaría de Movilidad de Bogotá")
                        .issueDate(LocalDate.of(2024, 1, 15))
                        .expiryDate(LocalDate.of(2025, 1, 14))
                        .fileUrl("https://storage.iqfleet.co/docs/to-abc123-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(4))
                        .uploadedBy("fabian.owner")
                        .vehicle(bus1)
                        .build()
        ));

        // --- XYZ-456 (Toyota HiAce) ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("SOAT XYZ-456")
                        .documentType(DocumentType.SOAT)
                        .referenceNumber("SOAT-2024-002345")
                        .issuingEntity("Sura")
                        .issueDate(LocalDate.of(2024, 3, 1))
                        .expiryDate(LocalDate.now().plusDays(20))
                        .fileUrl("https://storage.iqfleet.co/docs/soat-xyz456-2024.pdf")
                        .fileFormat("PDF")
                        .notes("VENCE PRONTO — renovar antes del " + LocalDate.now().plusDays(20))
                        .uploadDate(now.minusMonths(2))
                        .uploadedBy("andres.admin")
                        .vehicle(microbus)
                        .build(),

                Document.builder()
                        .name("RTM XYZ-456")
                        .documentType(DocumentType.RTM)
                        .referenceNumber("RTM-2023-009012")
                        .issuingEntity("Centro de Diagnóstico del Norte")
                        .issueDate(LocalDate.of(2023, 8, 10))
                        .expiryDate(LocalDate.of(2025, 8, 9))
                        .fileUrl("https://storage.iqfleet.co/docs/rtm-xyz456-2023.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(9))
                        .uploadedBy("andres.admin")
                        .vehicle(microbus)
                        .build(),

                Document.builder()
                        .name("Tarjeta de Propiedad XYZ-456")
                        .documentType(DocumentType.PROPERTY_CARD)
                        .issuingEntity("RUNT")
                        .issueDate(LocalDate.of(2021, 7, 20))
                        .uploadDate(now.minusYears(2))
                        .uploadedBy("fabian.owner")
                        .vehicle(microbus)
                        .build()
        ));

        // --- DEF-789 (Ford Transit) ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("SOAT DEF-789")
                        .documentType(DocumentType.SOAT)
                        .referenceNumber("SOAT-2024-003456")
                        .issuingEntity("Allianz")
                        .issueDate(LocalDate.of(2024, 1, 10))
                        .expiryDate(LocalDate.of(2025, 12, 31))
                        .fileUrl("https://storage.iqfleet.co/docs/soat-def789-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(4))
                        .uploadedBy("fabian.owner")
                        .vehicle(van)
                        .build(),

                Document.builder()
                        .name("RTM DEF-789")
                        .documentType(DocumentType.RTM)
                        .referenceNumber("RTM-2024-007890")
                        .issuingEntity("Centro de Diagnóstico Sur")
                        .issueDate(LocalDate.of(2024, 2, 1))
                        .expiryDate(LocalDate.of(2026, 1, 31))
                        .fileUrl("https://storage.iqfleet.co/docs/rtm-def789-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(3))
                        .uploadedBy("andres.admin")
                        .vehicle(van)
                        .build()
        ));

        // --- GHI-012 (Chevrolet NPR - en mantenimiento, SOAT vencido) ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("SOAT GHI-012")
                        .documentType(DocumentType.SOAT)
                        .referenceNumber("SOAT-2023-004567")
                        .issuingEntity("Seguros del Estado")
                        .issueDate(LocalDate.of(2023, 1, 5))
                        .expiryDate(LocalDate.of(2024, 1, 4))
                        .fileUrl("https://storage.iqfleet.co/docs/soat-ghi012-2023.pdf")
                        .fileFormat("PDF")
                        .notes("SOAT VENCIDO. Requiere renovación urgente.")
                        .uploadDate(now.minusYears(1).minusMonths(4))
                        .uploadedBy("fabian.owner")
                        .vehicle(bus2)
                        .build(),

                Document.builder()
                        .name("RTM GHI-012")
                        .documentType(DocumentType.RTM)
                        .referenceNumber("RTM-2023-006789")
                        .issuingEntity("Centro de Diagnóstico Occidente")
                        .issueDate(LocalDate.of(2023, 6, 20))
                        .expiryDate(LocalDate.of(2025, 6, 19))
                        .fileUrl("https://storage.iqfleet.co/docs/rtm-ghi012-2023.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusYears(1))
                        .uploadedBy("andres.admin")
                        .vehicle(bus2)
                        .build()
        ));

        // --- JKL-345 (Hyundai County) ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("SOAT JKL-345")
                        .documentType(DocumentType.SOAT)
                        .referenceNumber("SOAT-2024-005678")
                        .issuingEntity("Mapfre")
                        .issueDate(LocalDate.of(2024, 6, 1))
                        .expiryDate(LocalDate.of(2025, 5, 31))
                        .fileUrl("https://storage.iqfleet.co/docs/soat-jkl345-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(5))
                        .uploadedBy("andres.admin")
                        .vehicle(minibus)
                        .build(),

                Document.builder()
                        .name("RTM JKL-345")
                        .documentType(DocumentType.RTM)
                        .referenceNumber("RTM-2024-008901")
                        .issuingEntity("Centro de Diagnóstico Norte")
                        .issueDate(LocalDate.of(2024, 4, 15))
                        .expiryDate(LocalDate.of(2026, 4, 14))
                        .fileUrl("https://storage.iqfleet.co/docs/rtm-jkl345-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(1))
                        .uploadedBy("andres.admin")
                        .vehicle(minibus)
                        .build()
        ));
    }

    private void seedDriverDocuments(List<Driver> drivers) {
        Driver carlos = drivers.get(0);
        Driver luis   = drivers.get(1);
        Driver maria  = drivers.get(2);
        Driver jorge  = drivers.get(3);
        Driver ana    = drivers.get(4);

        LocalDateTime now = LocalDateTime.now();

        // --- Carlos Mendoza ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("Licencia de Conducción - Carlos Mendoza")
                        .documentType(DocumentType.DRIVING_LICENSE)
                        .referenceNumber("LIC-001-2019")
                        .issuingEntity("RUNT")
                        .issueDate(LocalDate.of(2019, 5, 10))
                        .expiryDate(LocalDate.now().plusYears(2))
                        .fileUrl("https://storage.iqfleet.co/docs/licencia-carlos-2019.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusYears(2))
                        .uploadedBy("fabian.owner")
                        .driver(carlos)
                        .build(),

                Document.builder()
                        .name("Certificado Médico - Carlos Mendoza")
                        .documentType(DocumentType.MEDICAL_CERTIFICATE)
                        .referenceNumber("CM-2024-001")
                        .issuingEntity("IPS Salud Total")
                        .issueDate(LocalDate.of(2024, 1, 20))
                        .expiryDate(LocalDate.of(2025, 1, 19))
                        .fileUrl("https://storage.iqfleet.co/docs/medico-carlos-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(4))
                        .uploadedBy("andres.admin")
                        .driver(carlos)
                        .build()
        ));

        // --- Luis García ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("Licencia de Conducción - Luis García")
                        .documentType(DocumentType.DRIVING_LICENSE)
                        .referenceNumber("LIC-002-2020")
                        .issuingEntity("RUNT")
                        .issueDate(LocalDate.of(2020, 3, 15))
                        .expiryDate(LocalDate.now().plusMonths(8))
                        .fileUrl("https://storage.iqfleet.co/docs/licencia-luis-2020.pdf")
                        .fileFormat("PDF")
                        .notes("Próximo vencimiento en 8 meses. Programar renovación.")
                        .uploadDate(now.minusYears(1))
                        .uploadedBy("andres.admin")
                        .driver(luis)
                        .build(),

                Document.builder()
                        .name("Certificado Médico - Luis García")
                        .documentType(DocumentType.MEDICAL_CERTIFICATE)
                        .referenceNumber("CM-2024-002")
                        .issuingEntity("Clínica del Norte")
                        .issueDate(LocalDate.of(2024, 2, 5))
                        .expiryDate(LocalDate.of(2025, 2, 4))
                        .fileUrl("https://storage.iqfleet.co/docs/medico-luis-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(3))
                        .uploadedBy("andres.admin")
                        .driver(luis)
                        .build()
        ));

        // --- María Rodríguez ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("Licencia de Conducción - María Rodríguez")
                        .documentType(DocumentType.DRIVING_LICENSE)
                        .referenceNumber("LIC-003-2021")
                        .issuingEntity("RUNT")
                        .issueDate(LocalDate.of(2021, 9, 22))
                        .expiryDate(LocalDate.now().plusYears(1).plusMonths(6))
                        .fileUrl("https://storage.iqfleet.co/docs/licencia-maria-2021.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusYears(1))
                        .uploadedBy("fabian.owner")
                        .driver(maria)
                        .build(),

                Document.builder()
                        .name("Certificado Médico - María Rodríguez")
                        .documentType(DocumentType.MEDICAL_CERTIFICATE)
                        .referenceNumber("CM-2024-003")
                        .issuingEntity("IPS SaludCoop")
                        .issueDate(LocalDate.of(2024, 3, 10))
                        .expiryDate(LocalDate.of(2025, 3, 9))
                        .fileUrl("https://storage.iqfleet.co/docs/medico-maria-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(2))
                        .uploadedBy("andres.admin")
                        .driver(maria)
                        .build()
        ));

        // --- Jorge Vargas (inactivo - licencia vencida) ---
        documentRepository.save(Document.builder()
                .name("Licencia de Conducción - Jorge Vargas")
                .documentType(DocumentType.DRIVING_LICENSE)
                .referenceNumber("LIC-004-2018")
                .issuingEntity("RUNT")
                .issueDate(LocalDate.of(2018, 11, 30))
                .expiryDate(LocalDate.now().minusMonths(3))
                .fileUrl("https://storage.iqfleet.co/docs/licencia-jorge-2018.pdf")
                .fileFormat("PDF")
                .notes("LICENCIA VENCIDA. Conductor desactivado hasta renovación.")
                .uploadDate(now.minusYears(2))
                .uploadedBy("fabian.owner")
                .driver(jorge)
                .build());

        // --- Ana Torres ---
        documentRepository.saveAll(List.of(
                Document.builder()
                        .name("Licencia de Conducción - Ana Torres")
                        .documentType(DocumentType.DRIVING_LICENSE)
                        .referenceNumber("LIC-005-2022")
                        .issuingEntity("RUNT")
                        .issueDate(LocalDate.of(2022, 7, 14))
                        .expiryDate(LocalDate.now().plusYears(3))
                        .fileUrl("https://storage.iqfleet.co/docs/licencia-ana-2022.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(10))
                        .uploadedBy("andres.admin")
                        .driver(ana)
                        .build(),

                Document.builder()
                        .name("Certificado Médico - Ana Torres")
                        .documentType(DocumentType.MEDICAL_CERTIFICATE)
                        .referenceNumber("CM-2024-004")
                        .issuingEntity("Médicos Unidos IPS")
                        .issueDate(LocalDate.of(2024, 4, 1))
                        .expiryDate(LocalDate.of(2025, 3, 31))
                        .fileUrl("https://storage.iqfleet.co/docs/medico-ana-2024.pdf")
                        .fileFormat("PDF")
                        .uploadDate(now.minusMonths(1))
                        .uploadedBy("andres.admin")
                        .driver(ana)
                        .build()
        ));
    }
}
