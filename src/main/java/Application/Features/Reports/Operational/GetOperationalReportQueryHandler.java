package Application.Features.Reports.Operational;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;

public class GetOperationalReportQueryHandler implements IRequestHandler<GetOperationalReportQuery, GetOperationalReportResponse> {
    @Override
    public Result<GetOperationalReportResponse>handle(GetOperationalReportQuery query){
        return Result.Success(new GetOperationalReportResponse(0,0, 0));
    }
}
