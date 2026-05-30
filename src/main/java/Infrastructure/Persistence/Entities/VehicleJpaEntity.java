package Infrastructure.Persistence.Entities;

import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "vehicles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_vehicle_plate", columnNames = "plate_number")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleJpaEntity extends BaseJpaEntity {

    @Column(name = "plate_number", nullable = false, length = 10)
    private String plateNumber;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "vehicle_model", nullable = false, length = 80)
    private String vehicleModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 15)
    private VehicleType vehicleType;

    @Column(name = "year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", foreignKey = @ForeignKey(name = "fk_vehicle_responsible_user"))
    private UserJpaEntity responsible;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "notes", length = 500)
    private String notes;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", foreignKey = @ForeignKey(name = "fk_vehicle_assigned_driver"))
    private DriverJpaEntity assignedDriver;

    @ToString.Exclude
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentJpaEntity> documents = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @Builder.Default
    private List<FinancialMovementJpaEntity> financialMovements = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AssignmentHistoryJpaEntity> assignmentHistory = new ArrayList<>();
}
