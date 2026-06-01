package Application.Features.Reports.Documentary;

import Application.Abstractions.IRequestHandler;
import Application.Result.Result;

public class GetDocumentaryReportQueryHandler
        implements IRequestHandler<
        GetDocumentaryReportQuery,
        GetDocumentaryReportResponse> {

    @Override
    public Result<GetDocumentaryReportResponse> handle(
            GetDocumentaryReportQuery query
    ) {

        return Result.Success(
                new GetDocumentaryReportResponse(
                        0,
                        0,
                        0
                )
        );
    }
}
