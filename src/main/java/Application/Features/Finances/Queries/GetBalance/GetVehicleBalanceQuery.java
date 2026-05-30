package Application.Features.Finances.Queries.GetBalance;

import Application.Abstractions.IQuery;
import Application.Features.Finances.Common.VehicleBalanceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetVehicleBalanceQuery implements IQuery<VehicleBalanceResponse> {
    private final UUID vehicleId;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
}
