package com.mobileshop.service.impl;

import com.mobileshop.dto.*;
import com.mobileshop.repository.DashboardRepository;
import com.mobileshop.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    private static final List<String> COLORS = List.of(
            "#4f8ef7", "#7c3aed", "#10b981", "#f59e0b", "#6b7280"
    );

    @Override
    public DashboardDTO getDashboardSummary() {
        Double todaySales = dashboardRepository.getTodaySales(LocalDate.now());
        if (todaySales == null) todaySales = 0.0;

        return DashboardDTO.builder()
                .todaySales(todaySales)
                .todayProfit(todaySales * 0.2)
                .totalProducts(dashboardRepository.getTotalProducts())
                .totalSuppliers(dashboardRepository.getTotalSuppliers())
                .lowStockProducts(dashboardRepository.getLowStockCount())
                .build();
    }

    @Override
    public WeeklySalesDTO getWeeklySales() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<Object[]> rows = dashboardRepository.getWeeklySales(weekAgo, today);

        Map<LocalDate, Double> salesMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            // PostgreSQL returns java.sql.Date for ::date cast
            LocalDate date;
            if (row[0] instanceof java.sql.Date sqlDate) {
                date = sqlDate.toLocalDate();
            } else if (row[0] instanceof LocalDate ld) {
                date = ld;
            } else {
                // fallback: parse from string
                date = LocalDate.parse(row[0].toString());
            }
            Double total = ((Number) row[1]).doubleValue();
            salesMap.put(date, total);
        }

        List<String> labels  = new ArrayList<>();
        List<Double> sales   = new ArrayList<>();
        List<Double> profits = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String label = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            Double daySales = salesMap.getOrDefault(date, 0.0);
            labels.add(label);
            sales.add(daySales);
            profits.add(daySales * 0.2);
        }

        return WeeklySalesDTO.builder()
                .labels(labels)
                .sales(sales)
                .profits(profits)
                .build();
    }

    @Override
    public List<LowStockProductDTO> getLowStockProductList() {
        List<Object[]> rows = dashboardRepository.getLowStockProductList();
        List<LowStockProductDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            // row[0]=name, row[1]=brand, row[2]=category, row[3]=stock
            String name     = (String) row[0];
            String brand    = (String) row[1];
            String category = (String) row[2];
            int stock       = ((Number) row[3]).intValue();

            result.add(LowStockProductDTO.builder()
                    .name(name)
                    .category(brand != null ? brand + " · " + category : category)
                    .stock(stock)
                    .build());
        }
        return result;
    }

    @Override
    public List<CategoryStatDTO> getCategoryStats() {
        List<Object[]> rows = dashboardRepository.getSalesByCategory();

        double total = rows.stream()
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .sum();

        List<CategoryStatDTO> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Object[] row    = rows.get(i);
            String catName  = (String) row[0];
            double revenue  = ((Number) row[1]).doubleValue();
            double pct      = total > 0 ? Math.round((revenue / total) * 100.0) : 0;

            result.add(CategoryStatDTO.builder()
                    .name(catName)
                    .pct(pct)
                    .color(COLORS.get(i % COLORS.size()))
                    .build());
        }
        return result;
    }
}