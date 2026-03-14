package com.mobileshop.service.impl;

import com.mobileshop.dto.PurchaseDTO;
import com.mobileshop.dto.PurchaseHistoryDTO;
import com.mobileshop.dto.PurchaseItemDTO;
import com.mobileshop.entity.*;
import com.mobileshop.repository.*;
import com.mobileshop.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository      purchaseRepository;
    private final SupplierRepository      supplierRepository;
    private final ProductRepository       productRepository;
    private final ProductImeiRepository   productImeiRepository;
    private final PurchaseItemRepository  purchaseItemRepository;
    private final InventoryLogRepository  inventoryLogRepository;

    @Override
    @Transactional
    public void createPurchase(PurchaseDTO dto) {

        // ✅ validation
        if (dto.getSupplierId() == null)
            throw new RuntimeException("Supplier is required");
        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new RuntimeException("At least one product is required");
        for (PurchaseItemDTO item : dto.getItems()) {
            if (item.getQuantity() <= 0)
                throw new RuntimeException("Quantity must be greater than 0");
            if (item.getPurchasePrice() <= 0)
                throw new RuntimeException("Purchase price must be greater than 0");
        }

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Purchase purchase = Purchase.builder()
                .supplier(supplier)
                .invoiceNumber(dto.getInvoiceNumber())
                .purchaseDate(dto.getPurchaseDate())
                .build();

        purchase = purchaseRepository.save(purchase);

        for (PurchaseItemDTO itemDTO : dto.getItems()) {

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product not found: " + itemDTO.getProductId()));

            // ✅ save purchase item
            PurchaseItem item = PurchaseItem.builder()
                    .purchase(purchase)
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .purchasePrice(itemDTO.getPurchasePrice())
                    .build();

            purchaseItemRepository.save(item);

            // update stock
            int newStock = product.getStock() + itemDTO.getQuantity();
            product.setStock(newStock);
            productRepository.save(product);

            // ✅ inventory log
            inventoryLogRepository.save(InventoryLog.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .remainingStock(newStock)
                    .movementType(MovementType.PURCHASE)
                    .movementDate(LocalDateTime.now())
                    .build());

            // handle IMEI
            if (product.getTrackingType() == TrackingType.IMEI) {
                if (itemDTO.getImeiNumbers() == null ||
                        itemDTO.getImeiNumbers().size() != itemDTO.getQuantity()) {
                    throw new RuntimeException(
                            "IMEI count must match quantity for: " + product.getName());
                }
                for (String imei : itemDTO.getImeiNumbers()) {
                    productImeiRepository.save(ProductImei.builder()
                            .imeiNumber(imei)
                            .product(product)
                            .status(ImeiStatus.IN_STOCK)
                            .build());
                }
            }
        }
    }

    @Override
    public List<PurchaseHistoryDTO> getAllPurchases() {
        return purchaseRepository.findAllWithItems()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseHistoryDTO> getPurchasesByDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to))
            throw new RuntimeException("From date cannot be after To date");
        return purchaseRepository.findByDateRange(from, to)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PurchaseHistoryDTO toDTO(Purchase p) {
        double total = p.getItems() != null
                ? p.getItems().stream()
                .mapToDouble(i -> i.getPurchasePrice() * i.getQuantity())
                .sum()
                : 0.0;

        return PurchaseHistoryDTO.builder()
                .id(p.getId())
                .purchaseDate(p.getPurchaseDate())
                .invoiceNumber(p.getInvoiceNumber())
                .supplierName(p.getSupplier() != null ? p.getSupplier().getName() : "")
                .itemCount(p.getItems() != null ? p.getItems().size() : 0)
                .totalAmount(total)
                .build();
    }
}