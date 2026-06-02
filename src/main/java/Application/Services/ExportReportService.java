package Application.Services;

import Application.Features.Reports.Documentary.GetDocumentaryReportResponse;
import Application.Features.Reports.Financial.GetFinancialReportResponse;
import Application.Features.Reports.Operational.GetOperationalReportResponse;

public interface ExportReportService {
    byte[] exportFinancial(GetFinancialReportResponse report, String format) throws Exception;
    byte[] exportDocumentary(GetDocumentaryReportResponse report, String format) throws Exception;
    byte[] exportOperational(GetOperationalReportResponse report, String format) throws Exception;
}