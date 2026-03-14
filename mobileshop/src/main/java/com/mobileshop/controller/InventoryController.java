package com.mobileshop.controller;

import com.mobileshop.dto.InventoryDTO;
import com.mobileshop.entity.InventoryLog;
import com.mobileshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryDTO> getInventory() {

        return inventoryService.getCurrentInventory();
    }

    @GetMapping("/history/{productId}")
    public List<InventoryLog> getProductHistory(@PathVariable Long productId) {

        return inventoryService.getProductHistory(productId);
    }

    @PostMapping("/backfill")
    public String backfillLogs() {
        inventoryService.backfillInventoryLogs();
        return "Backfill complete";
    }
}