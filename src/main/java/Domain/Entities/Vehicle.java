package Domain.Entities;

import Domain.Enums.VehicleStatus;
import Domain.Enums.VehicleType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends Entity {
    private String plateNumber;
    private String brand;
    private String vehicleModel;
    private VehicleType vehicleType;
    private Integer year;
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;
    private UUID responsibleId;
    private LocalDate registrationDate;
    private String notes;
    private UUID assignedDriverId;
}
