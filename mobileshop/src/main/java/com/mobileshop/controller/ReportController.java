package com.mobileshop.controller;

import com.mobileshop.report.dto.*;
import com.mobileshop.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily-sales")
    public List<DailySalesReportDTO> getDailySalesReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return reportService.getDailySalesReport(from, to);
    }

    @GetMapping("/product-sales")
    public List<ProductSalesReportDTO> getProductSalesReport() {
        return reportService.getProductSalesReport();
    }

    @GetMapping("/weekly-summary")
    public List<WeeklySummaryReportDTO> getWeeklySummary() {
        return reportService.getWeeklySummaryReport();
    }

    @GetMapping("/payment-mode")
    public List<PaymentModeReportDTO> getPaymentModeReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return reportService.getPaymentModeReport(from, to);
    }

    @GetMapping("/profit-loss")
    public List<ProfitLossReportDTO> getProfitLossReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return reportService.getProfitLossReport(from, to);
    }

    @GetMapping("/supplier-purchases")
    public List<SupplierPurchaseReportDTO> getSupplierPurchaseReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return reportService.getSupplierPurchaseReport(from, to);
    }
}