package com.mobileshop.report.service;

import com.mobileshop.report.dto.*;
import java.util.List;

public interface ReportService {
    List<DailySalesReportDTO>      getDailySalesReport(String from, String to);
    List<ProductSalesReportDTO>    getProductSalesReport();
    List<WeeklySummaryReportDTO>   getWeeklySummaryReport();
    List<PaymentModeReportDTO>     getPaymentModeReport(String from, String to);
    List<ProfitLossReportDTO>      getProfitLossReport(String from, String to);
    List<SupplierPurchaseReportDTO> getSupplierPurchaseReport(String from, String to);
}