package com.mobileshop.service;

import com.mobileshop.dto.PurchaseDTO;
import com.mobileshop.dto.PurchaseHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    void createPurchase(PurchaseDTO dto);
    List<PurchaseHistoryDTO> getAllPurchases();
    List<PurchaseHistoryDTO> getPurchasesByDateRange(LocalDate from, LocalDate to);
}