package com.mobileshop.controller;

import com.mobileshop.dto.*;
import com.mobileshop.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDTO getDashboard() {
        return dashboardService.getDashboardSummary();
    }

    @GetMapping("/weekly-sales")
    public WeeklySalesDTO getWeeklySales() {
        return dashboardService.getWeeklySales();
    }

    @GetMapping("/low-stock")
    public List<LowStockProductDTO> getLowStockProducts() {
        return dashboardService.getLowStockProductList();
    }

    @GetMapping("/category-stats")
    public List<CategoryStatDTO> getCategoryStats() {
        return dashboardService.getCategoryStats();
    }
}