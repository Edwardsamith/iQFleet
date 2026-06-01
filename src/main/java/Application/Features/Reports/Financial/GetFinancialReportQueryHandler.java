package Application.Features.Reports.Financial;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;

import java.math.BigDecimal;

public class GetFinancialReportQueryHandler implements IRequestHandler<
        GetFinancialReportQuery,
        GetFinancialReportResponse> {
    @Override
    public Result<GetFinancialReportResponse>handle(GetFinancialReportQuery query){
        return Result.Success(new GetFinancialReportResponse(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO));
    }
}
