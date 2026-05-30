package Application.Features.Finances.Commands.Register;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;
import Application.Result.Unit;
import Domain.Entities.FinancialMovement;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Repositories.DriverRepository;
import Domain.Repositories.FinancialMovementRepository;
import Domain.Repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RegisterMovementCommandHandler
        implements IRequestHandler<RegisterMovementCommand, Unit> {

    private final FinancialMovementRepository financialMovementRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    @Override
    public Result<Unit> handle(RegisterMovementCommand command) {

        if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.Failure("El monto debe ser mayor que cero");
        }

        if (command.getDate() != null && command.getDate().isAfter(LocalDate.now())) {
            return Result.Failure("El movimiento no puede tener una fecha futura");
        }

        if (!vehicleRepository.existsById(command.getVehicleId())) {
            return Result.Failure("El vehiculo especificado no existe");
        }

        if (command.getDriverId() != null && !driverRepository.existsById(command.getDriverId())) {
            return Result.Failure("El conductor especificado no existe");
        }

        if (!isCategoryValidForType(command.getCategory(), command.getMovementType())) {
            return Result.Failure("La categoría no coincide con el tipo de movimiento seleccionado");
        }

        FinancialMovement movement = FinancialMovement.builder()
                .movementType(command.getMovementType())
                .category(command.getCategory())
                .paymentMethod(command.getPaymentMethod())
                .amount(command.getAmount())
                .date(command.getDate() != null ? command.getDate() : LocalDate.now())
                .description(command.getDescription())
                .notes(command.getNotes())
                .vehicleId(command.getVehicleId())
                .driverId(command.getDriverId())
                .registeredBy(command.getRegisteredBy())
                .build();

        financialMovementRepository.save(movement);

        return Result.Success();
    }

    private boolean isCategoryValidForType(MovementCategory category, MovementType type) {
        if (type == MovementType.INCOME) {
            return category == MovementCategory.DAILY_COLLECTION
                    || category == MovementCategory.SUBSIDY
                    || category == MovementCategory.OTHER_INCOME;
        } else {
            return category == MovementCategory.FUEL
                    || category == MovementCategory.PREVENTIVE_MAINTENANCE
                    || category == MovementCategory.CORRECTIVE_MAINTENANCE
                    || category == MovementCategory.SALARY
                    || category == MovementCategory.INSURANCE
                    || category == MovementCategory.PAPERWORK
                    || category == MovementCategory.TAX
                    || category == MovementCategory.OTHER_EXPENSE;
        }
    }
}