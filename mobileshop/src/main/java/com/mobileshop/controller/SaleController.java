package com.mobileshop.controller;

import com.mobileshop.dto.SaleDTO;
import com.mobileshop.dto.SaleHistoryDTO;
import com.mobileshop.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public String createSale(@RequestBody SaleDTO dto) {
        saleService.createSale(dto);
        return "Sale Recorded Successfully";
    }

    @GetMapping
    public List<SaleHistoryDTO> getAllSales(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        if (from != null && to != null) {
            return saleService.getSalesByDateRange(
                    LocalDate.parse(from),
                    LocalDate.parse(to));
        }
        return saleService.getAllSales();
    }
}