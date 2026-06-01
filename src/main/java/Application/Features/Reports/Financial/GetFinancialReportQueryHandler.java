package Application.Features.Reports.Financial;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;

public class GetFinancialReportQueryHandler implements IRequestHandler<
        GetFinancialReportQuery,
        GetFinancialReportResponse> {
    @Override
    public Result<GetFinancialReportResponse>handle(GetFinancialReportQuery query){
        return Result.Success(new GetFinancialReportResponse(0,0,0));
    }
}
