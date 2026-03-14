package com.mobileshop.service;

import com.mobileshop.dto.InventoryDTO;
import com.mobileshop.entity.InventoryLog;

import java.util.List;

public interface InventoryService {

    List<InventoryDTO> getCurrentInventory();
    void backfillInventoryLogs();
    List<InventoryLog> getProductHistory(Long productId);

}