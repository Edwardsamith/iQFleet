package Domain.Entities;

import Domain.Enums.DriverStatus;
import Domain.Enums.IdentificationType;
import Domain.Enums.LicenseCategory;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends Entity {
    private IdentificationType identificationType;
    private String identificationNumber;
    private String firstName;
    private String lastName;
    private String licenseNumber;
    private LicenseCategory licenseCategory;
    private LocalDate licenseExpiry;
    private String phone;
    private String email;
    private String address;
    @Builder.Default
    private DriverStatus status = DriverStatus.ACTIVE;
    private LocalDate registrationDate;
    private LocalDate deactivationDate;
    private String deactivationReason;
}
