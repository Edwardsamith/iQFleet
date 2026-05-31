package Application.Features.Finances.Queries.GetBalance;

import Application.Abstractions.IQuery;
import Application.Features.Finances.Common.FleetBalanceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GetFleetBalanceQuery implements IQuery<FleetBalanceResponse> {
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
}
