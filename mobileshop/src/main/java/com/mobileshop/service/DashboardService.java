package com.mobileshop.service;

import com.mobileshop.dto.*;
import java.util.List;

public interface DashboardService {
    DashboardDTO getDashboardSummary();
    WeeklySalesDTO getWeeklySales();
    List<LowStockProductDTO> getLowStockProductList();
    List<CategoryStatDTO> getCategoryStats();
}