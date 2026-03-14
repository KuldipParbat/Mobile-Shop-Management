package com.mobileshop.report.service.impl;

import com.mobileshop.report.dto.*;
import com.mobileshop.report.repository.ReportRepository;
import com.mobileshop.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    // ── date cast helper ──
    private LocalDate toLocalDate(Object obj) {
        if (obj instanceof java.sql.Date d) return d.toLocalDate();
        if (obj instanceof LocalDate d)     return d;
        return LocalDate.parse(obj.toString());
    }

    // ── validate date range ──
    private void validateRange(String from, String to) {
        if (from != null && to != null && from.compareTo(to) > 0)
            throw new RuntimeException("From date cannot be after To date");
    }

    @Override
    public List<DailySalesReportDTO> getDailySalesReport(String from, String to) {
        validateRange(from, to);
        return reportRepository.getDailySalesReport(from, to)
                .stream()
                .map(obj -> DailySalesReportDTO.builder()
                        .date(toLocalDate(obj[0]))
                        .totalSales(((Number) obj[1]).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductSalesReportDTO> getProductSalesReport() {
        return reportRepository.getProductSalesReport()
                .stream()
                .map(obj -> ProductSalesReportDTO.builder()
                        .productName((String) obj[0])
                        .totalSold(((Number) obj[1]).longValue())
                        .revenue(((Number) obj[2]).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<WeeklySummaryReportDTO> getWeeklySummaryReport() {
        return reportRepository.getWeeklySummaryReport()
                .stream()
                .map(obj -> WeeklySummaryReportDTO.builder()
                        .week((String) obj[0])
                        .totalSales(((Number) obj[1]).doubleValue())
                        .totalProfit(((Number) obj[2]).doubleValue())
                        .totalOrders(((Number) obj[3]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentModeReportDTO> getPaymentModeReport(String from, String to) {
        validateRange(from, to);
        return reportRepository.getPaymentModeReport(from, to)
                .stream()
                .map(obj -> PaymentModeReportDTO.builder()
                        .paymentMode((String) obj[0])
                        .totalOrders(((Number) obj[1]).longValue())
                        .totalAmount(((Number) obj[2]).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfitLossReportDTO> getProfitLossReport(String from, String to) {
        validateRange(from, to);
        return reportRepository.getProfitLossReport(from, to)
                .stream()
                .map(obj -> {
                    double revenue = ((Number) obj[1]).doubleValue();
                    double cost    = ((Number) obj[2]).doubleValue();
                    double profit  = revenue - cost;
                    double margin  = revenue > 0 ? (profit / revenue) * 100 : 0;
                    return ProfitLossReportDTO.builder()
                            .date(toLocalDate(obj[0]))
                            .totalRevenue(revenue)
                            .totalCost(cost)
                            .profit(profit)
                            .margin(Math.round(margin * 10.0) / 10.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierPurchaseReportDTO> getSupplierPurchaseReport(String from, String to) {
        validateRange(from, to);
        return reportRepository.getSupplierPurchaseReport(from, to)
                .stream()
                .map(obj -> SupplierPurchaseReportDTO.builder()
                        .supplierName((String) obj[0])
                        .totalPurchases(((Number) obj[1]).longValue())
                        .totalAmount(((Number) obj[2]).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }
}