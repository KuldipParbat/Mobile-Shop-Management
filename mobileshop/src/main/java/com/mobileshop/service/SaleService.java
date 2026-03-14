package com.mobileshop.service;

import com.mobileshop.dto.SaleDTO;
import com.mobileshop.dto.SaleHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface SaleService {
    void createSale(SaleDTO dto);
    List<SaleHistoryDTO> getAllSales();
    List<SaleHistoryDTO> getSalesByDateRange(LocalDate from, LocalDate to);
}