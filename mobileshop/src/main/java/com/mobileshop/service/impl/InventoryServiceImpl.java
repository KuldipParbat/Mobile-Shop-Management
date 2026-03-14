package com.mobileshop.service.impl;

import com.mobileshop.dto.InventoryDTO;
import com.mobileshop.entity.*;
import com.mobileshop.repository.*;
import com.mobileshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final SaleItemRepository saleItemRepository;

    @Override
    public List<InventoryDTO> getCurrentInventory() {
        return productRepository.findAll()
                .stream()
                .map(p -> InventoryDTO.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .brand(p.getBrand())
                        .category(p.getCategory())
                        .trackingType(p.getTrackingType().name())
                        .stock(p.getStock())
                        .purchasePrice(p.getPurchasePrice())
                        .sellingPrice(p.getSellingPrice())
                        .stockValue(p.getStock() * p.getPurchasePrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryLog> getProductHistory(Long productId) {
        return inventoryLogRepository
                .findByProductIdOrderByMovementDateDesc(productId);
    }

    @Override
    public void backfillInventoryLogs() {
        // backfill purchase logs from existing purchase_items
        purchaseItemRepository.findAll().forEach(item -> {
            boolean exists = inventoryLogRepository
                    .existsByProductIdAndMovementType(
                            item.getProduct().getId(), MovementType.PURCHASE);
            if (!exists) {
                inventoryLogRepository.save(InventoryLog.builder()
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .remainingStock(item.getProduct().getStock())
                        .movementType(MovementType.PURCHASE)
                        .movementDate(LocalDateTime.now())
                        .build());
            }
        });

        // backfill sale logs from existing sale_items
        saleItemRepository.findAll().forEach(item -> {
            boolean exists = inventoryLogRepository
                    .existsByProductIdAndMovementType(
                            item.getProduct().getId(), MovementType.SALE);
            if (!exists) {
                inventoryLogRepository.save(InventoryLog.builder()
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .remainingStock(item.getProduct().getStock())
                        .movementType(MovementType.SALE)
                        .movementDate(LocalDateTime.now())
                        .build());
            }
        });
    }
}