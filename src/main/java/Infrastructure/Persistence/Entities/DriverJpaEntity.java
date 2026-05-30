package Infrastructure.Persistence.Entities;

import Domain.Enums.DriverStatus;
import Domain.Enums.IdentificationType;
import Domain.Enums.LicenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "drivers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_driver_identification", columnNames = {"identification_type", "identification_number"}),
        @UniqueConstraint(name = "uk_driver_license",        columnNames = "license_number"),
        @UniqueConstraint(name = "uk_driver_email",          columnNames = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverJpaEntity extends BaseJpaEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false, length = 3)
    private IdentificationType identificationType;

    @Column(name = "identification_number", nullable = false, length = 12)
    private String identificationNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "license_number", nullable = false, length = 20)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_category", nullable = false, length = 3)
    private LicenseCategory licenseCategory;

    @Column(name = "license_expiry", nullable = false)
    private LocalDate licenseExpiry;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private DriverStatus status = DriverStatus.ACTIVE;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "deactivation_date")
    private LocalDate deactivationDate;

    @Column(name = "deactivation_reason", length = 300)
    private String deactivationReason;

    @ToString.Exclude
    @OneToOne(mappedBy = "assignedDriver", fetch = FetchType.LAZY)
    private VehicleJpaEntity currentVehicle;
}
