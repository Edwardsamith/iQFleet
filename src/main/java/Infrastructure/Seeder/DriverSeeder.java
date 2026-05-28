package Infrastructure.Seeder;

import Domain.Entities.Driver;
import Domain.Enums.DriverStatus;
import Domain.Enums.IdentificationType;
import Domain.Enums.LicenseCategory;
import Domain.Repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverSeeder {

    private final DriverRepository driverRepository;

    /**
     * @return [0]=Carlos, [1]=Luis, [2]=María, [3]=Jorge(inactivo), [4]=Ana
     */
    public List<Driver> seed() {
        List<Driver> drivers = List.of(
                driverRepository.save(Driver.builder()
                        .identificationType(IdentificationType.CC)
                        .identificationNumber("1234567890")
                        .firstName("Carlos")
                        .lastName("Mendoza")
                        .licenseNumber("LIC-001-2019")
                        .licenseCategory(LicenseCategory.C1)
                        .licenseExpiry(LocalDate.now().plusYears(2))
                        .phone("3151234567")
                        .email("carlos.mendoza@gmail.com")
                        .address("Cra 15 #45-23, Bogotá")
                        .status(DriverStatus.ACTIVE)
                        .registrationDate(LocalDate.now().minusYears(3))
                        .build()),

                driverRepository.save(Driver.builder()
                        .identificationType(IdentificationType.CC)
                        .identificationNumber("0987654321")
                        .firstName("Luis")
                        .lastName("García")
                        .licenseNumber("LIC-002-2020")
                        .licenseCategory(LicenseCategory.B2)
                        .licenseExpiry(LocalDate.now().plusMonths(8))
                        .phone("3209876543")
                        .email("luis.garcia@gmail.com")
                        .address("Cll 72 #10-15, Bogotá")
                        .status(DriverStatus.ACTIVE)
                        .registrationDate(LocalDate.now().minusYears(2))
                        .build()),

                driverRepository.save(Driver.builder()
                        .identificationType(IdentificationType.CE)
                        .identificationNumber("5566778899")
                        .firstName("María")
                        .lastName("Rodríguez")
                        .licenseNumber("LIC-003-2021")
                        .licenseCategory(LicenseCategory.B1)
                        .licenseExpiry(LocalDate.now().plusYears(1).plusMonths(6))
                        .phone("3114455667")
                        .email("maria.rodriguez@gmail.com")
                        .address("Av. 68 #30-40, Bogotá")
                        .status(DriverStatus.ACTIVE)
                        .registrationDate(LocalDate.now().minusYears(1).minusMonths(6))
                        .build()),

                driverRepository.save(Driver.builder()
                        .identificationType(IdentificationType.CC)
                        .identificationNumber("1122334455")
                        .firstName("Jorge")
                        .lastName("Vargas")
                        .licenseNumber("LIC-004-2018")
                        .licenseCategory(LicenseCategory.C2)
                        .licenseExpiry(LocalDate.now().minusMonths(3))
                        .phone("3006677889")
                        .email("jorge.vargas@gmail.com")
                        .address("Cll 100 #50-60, Bogotá")
                        .status(DriverStatus.INACTIVE)
                        .registrationDate(LocalDate.now().minusYears(4))
                        .deactivationDate(LocalDate.now().minusMonths(2))
                        .deactivationReason("Licencia vencida. Pendiente renovación ante el RUNT.")
                        .build()),

                driverRepository.save(Driver.builder()
                        .identificationType(IdentificationType.CC)
                        .identificationNumber("9988776655")
                        .firstName("Ana")
                        .lastName("Torres")
                        .licenseNumber("LIC-005-2022")
                        .licenseCategory(LicenseCategory.B3)
                        .licenseExpiry(LocalDate.now().plusYears(3))
                        .phone("3185544332")
                        .email("ana.torres@gmail.com")
                        .address("Cra 30 #60-80, Bogotá")
                        .status(DriverStatus.ACTIVE)
                        .registrationDate(LocalDate.now().minusMonths(10))
                        .build())
        );

        log.info("{} conductores creados.", drivers.size());
        return drivers;
    }
}
