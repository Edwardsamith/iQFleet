package Application.Services

import Application.Features.Reports.Documentary.GetDocumentaryReportResponse
import Application.Features.Reports.Financial.GetFinancialReportResponse
import Application.Features.Reports.Operational.GetOperationalReportResponse

interface ReportExportService {
    @Throws(Exception::class)
    fun exportFinancial(report: GetFinancialReportResponse?, format: String?): ByteArray?

    @Throws(Exception::class)
    fun exportDocumentary(report: GetDocumentaryReportResponse?, format: String?): ByteArray?

    @Throws(Exception::class)
    fun exportOperational(report: GetOperationalReportResponse?, format: String?): ByteArray?
}