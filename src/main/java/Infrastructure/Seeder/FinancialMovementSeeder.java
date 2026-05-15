package Infrastructure.Seeder;

import Domain.Entities.Driver;
import Domain.Entities.FinancialMovement;
import Domain.Entities.Vehicle;
import Domain.Enums.MovementCategory;
import Domain.Enums.MovementType;
import Domain.Enums.PaymentMethod;
import Infrastructure.Repositories.JpaFinancialMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialMovementSeeder {

    private final JpaFinancialMovementRepository financialMovementRepository;

    /**
     * @param drivers  [0]=Carlos, [1]=Luis, [2]=María, [3]=Jorge, [4]=Ana
     * @param vehicles [0]=bus1, [1]=microbus, [2]=van, [3]=bus2, [4]=minibus
     */
    public void seed(List<Driver> drivers, List<Vehicle> vehicles) {
        Driver carlos = drivers.get(0);
        Driver luis   = drivers.get(1);
        Driver maria  = drivers.get(2);
        Driver ana    = drivers.get(4);

        Vehicle bus1     = vehicles.get(0);
        Vehicle microbus = vehicles.get(1);
        Vehicle van      = vehicles.get(2);
        Vehicle bus2     = vehicles.get(3);
        Vehicle minibus  = vehicles.get(4);

        LocalDate today = LocalDate.now();
        List<FinancialMovement> movements = new ArrayList<>();

        movements.addAll(buildBus1Movements(bus1, carlos, today));
        movements.addAll(buildMicrobusMovements(microbus, luis, today));
        movements.addAll(buildVanMovements(van, maria, today));
        movements.addAll(buildBus2Movements(bus2, today));
        movements.addAll(buildMinibusMovements(minibus, ana, today));

        financialMovementRepository.saveAll(movements);
        log.info("{} movimientos financieros creados.", movements.size());
    }

    private List<FinancialMovement> buildBus1Movements(Vehicle bus1, Driver carlos, LocalDate today) {
        List<FinancialMovement> list = new ArrayList<>();

        // Recaudos diarios lun-sáb (últimos 60 días)
        for (int i = 60; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            if (date.getDayOfWeek().getValue() <= 6) {
                list.add(FinancialMovement.builder()
                        .movementType(MovementType.INCOME)
                        .category(MovementCategory.DAILY_COLLECTION)
                        .paymentMethod(PaymentMethod.CASH)
                        .amount(new BigDecimal("180000.00"))
                        .date(date)
                        .description("Recaudo diario ruta norte")
                        .vehicle(bus1)
                        .driver(carlos)
                        .registeredBy("andres.admin")
                        .build());
            }
        }

        // Combustible mensual
        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.FUEL)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("320000.00"))
                .date(today.minusMonths(2).withDayOfMonth(5))
                .description("Carga de combustible — estación Terpel")
                .vehicle(bus1)
                .registeredBy("andres.admin")
                .build());

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.FUEL)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("310000.00"))
                .date(today.minusMonths(1).withDayOfMonth(5))
                .description("Carga de combustible — estación Terpel")
                .vehicle(bus1)
                .registeredBy("andres.admin")
                .build());

        // Salario mensual
        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.SALARY)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("1500000.00"))
                .date(today.minusMonths(2).withDayOfMonth(28))
                .description("Pago nómina conductor")
                .vehicle(bus1)
                .driver(carlos)
                .registeredBy("fabian.owner")
                .build());

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.SALARY)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("1500000.00"))
                .date(today.minusMonths(1).withDayOfMonth(28))
                .description("Pago nómina conductor")
                .vehicle(bus1)
                .driver(carlos)
                .registeredBy("fabian.owner")
                .build());

        return list;
    }

    private List<FinancialMovement> buildMicrobusMovements(Vehicle microbus, Driver luis, LocalDate today) {
        List<FinancialMovement> list = new ArrayList<>();

        // Recaudos diarios lun-vie (últimos 60 días)
        for (int i = 60; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            if (date.getDayOfWeek().getValue() <= 5) {
                list.add(FinancialMovement.builder()
                        .movementType(MovementType.INCOME)
                        .category(MovementCategory.DAILY_COLLECTION)
                        .paymentMethod(PaymentMethod.CASH)
                        .amount(new BigDecimal("130000.00"))
                        .date(date)
                        .description("Recaudo diario ruta centro")
                        .vehicle(microbus)
                        .driver(luis)
                        .registeredBy("andres.admin")
                        .build());
            }
        }

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.PREVENTIVE_MAINTENANCE)
                .paymentMethod(PaymentMethod.CASH)
                .amount(new BigDecimal("450000.00"))
                .date(today.minusMonths(1).withDayOfMonth(12))
                .description("Cambio de aceite y filtros — 30.000 km")
                .vehicle(microbus)
                .registeredBy("andres.admin")
                .build());

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.SALARY)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("1300000.00"))
                .date(today.minusMonths(1).withDayOfMonth(28))
                .description("Pago nómina conductor")
                .vehicle(microbus)
                .driver(luis)
                .registeredBy("fabian.owner")
                .build());

        return list;
    }

    private List<FinancialMovement> buildVanMovements(Vehicle van, Driver maria, LocalDate today) {
        List<FinancialMovement> list = new ArrayList<>();

        // Recaudos diarios lun-vie (últimos 60 días)
        for (int i = 60; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            if (date.getDayOfWeek().getValue() <= 5) {
                list.add(FinancialMovement.builder()
                        .movementType(MovementType.INCOME)
                        .category(MovementCategory.DAILY_COLLECTION)
                        .paymentMethod(PaymentMethod.CASH)
                        .amount(new BigDecimal("150000.00"))
                        .date(date)
                        .description("Recaudo diario ruta sur")
                        .vehicle(van)
                        .driver(maria)
                        .registeredBy("andres.admin")
                        .build());
            }
        }

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.SALARY)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("1400000.00"))
                .date(today.minusMonths(1).withDayOfMonth(28))
                .description("Pago nómina conductor")
                .vehicle(van)
                .driver(maria)
                .registeredBy("fabian.owner")
                .build());

        return list;
    }

    private List<FinancialMovement> buildBus2Movements(Vehicle bus2, LocalDate today) {
        return List.of(
                FinancialMovement.builder()
                        .movementType(MovementType.EXPENSE)
                        .category(MovementCategory.CORRECTIVE_MAINTENANCE)
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
                        .amount(new BigDecimal("1200000.00"))
                        .date(today.minusDays(5))
                        .description("Reparación sistema de frenos — Taller Automotriz Central")
                        .notes("Incluye: pastillas, discos y bomba de frenos.")
                        .vehicle(bus2)
                        .registeredBy("andres.admin")
                        .build(),

                FinancialMovement.builder()
                        .movementType(MovementType.EXPENSE)
                        .category(MovementCategory.INSURANCE)
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
                        .amount(new BigDecimal("850000.00"))
                        .date(today.minusMonths(3).withDayOfMonth(1))
                        .description("Póliza de responsabilidad civil extracontractual")
                        .vehicle(bus2)
                        .registeredBy("fabian.owner")
                        .build()
        );
    }

    private List<FinancialMovement> buildMinibusMovements(Vehicle minibus, Driver ana, LocalDate today) {
        List<FinancialMovement> list = new ArrayList<>();

        // Recaudos diarios lun-sáb (últimos 60 días)
        for (int i = 60; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            if (date.getDayOfWeek().getValue() <= 6) {
                list.add(FinancialMovement.builder()
                        .movementType(MovementType.INCOME)
                        .category(MovementCategory.DAILY_COLLECTION)
                        .paymentMethod(PaymentMethod.CASH)
                        .amount(new BigDecimal("110000.00"))
                        .date(date)
                        .description("Recaudo diario ruta occidente")
                        .vehicle(minibus)
                        .driver(ana)
                        .registeredBy("andres.admin")
                        .build());
            }
        }

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.SALARY)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("1250000.00"))
                .date(today.minusMonths(1).withDayOfMonth(28))
                .description("Pago nómina conductor")
                .vehicle(minibus)
                .driver(ana)
                .registeredBy("fabian.owner")
                .build());

        list.add(FinancialMovement.builder()
                .movementType(MovementType.EXPENSE)
                .category(MovementCategory.TAX)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("380000.00"))
                .date(today.minusMonths(2).withDayOfMonth(15))
                .description("Impuesto de rodamiento anual")
                .vehicle(minibus)
                .registeredBy("fabian.owner")
                .build());

        return list;
    }
}
