package com.mobileshop.controller;

import com.mobileshop.dto.PurchaseDTO;
import com.mobileshop.dto.PurchaseHistoryDTO;
import com.mobileshop.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public String createPurchase(@RequestBody PurchaseDTO dto) {
        purchaseService.createPurchase(dto);
        return "Purchase Saved Successfully";
    }

    @GetMapping
    public List<PurchaseHistoryDTO> getAllPurchases(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        if (from != null && to != null) {
            return purchaseService.getPurchasesByDateRange(
                    LocalDate.parse(from),
                    LocalDate.parse(to));
        }
        return purchaseService.getAllPurchases();
    }
}