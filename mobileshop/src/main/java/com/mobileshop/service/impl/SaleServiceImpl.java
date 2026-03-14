package com.mobileshop.service.impl;

import com.mobileshop.dto.SaleDTO;
import com.mobileshop.dto.SaleHistoryDTO;
import com.mobileshop.dto.SaleItemDTO;
import com.mobileshop.entity.*;
import com.mobileshop.repository.*;
import com.mobileshop.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ProductImeiRepository productImeiRepository;
    private final SaleItemRepository saleItemRepository;
    private final InventoryLogRepository inventoryLogRepository;

    @Override
    @Transactional
    public void createSale(SaleDTO dto) {

        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new RuntimeException("At least one item is required");

        Sale sale = Sale.builder()
                .saleDate(dto.getSaleDate())
                .paymentMode(dto.getPaymentMode())
                .totalAmount(0.0)
                .build();

        sale = saleRepository.save(sale);
        double total = 0;

        for (SaleItemDTO itemDTO : dto.getItems()) {

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product not found: " + itemDTO.getProductId()));

            // ✅ stock validation
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for '" + product.getName() + "'. " +
                                "Available: " + product.getStock() +
                                ", Requested: " + itemDTO.getQuantity());
            }

            // ✅ IMEI pre-validation
            if (product.getTrackingType() == TrackingType.IMEI) {
                if (itemDTO.getImeiNumbers() == null || itemDTO.getImeiNumbers().isEmpty())
                    throw new RuntimeException("IMEI required for: " + product.getName());
                for (String imei : itemDTO.getImeiNumbers()) {
                    productImeiRepository
                            .findByImeiNumberAndStatus(imei, ImeiStatus.IN_STOCK)
                            .orElseThrow(() -> new RuntimeException(
                                    "IMEI not found or already sold: " + imei));
                }
            }

            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .sellingPrice(itemDTO.getSellingPrice())
                    .discount(itemDTO.getDiscount() != null ? itemDTO.getDiscount() : 0.0)
                    .build();

            saleItemRepository.save(item);

            double itemTotal = (itemDTO.getSellingPrice() * itemDTO.getQuantity())
                    - (itemDTO.getDiscount() != null ? itemDTO.getDiscount() : 0.0);
            total += itemTotal;

            int newStock = product.getStock() - itemDTO.getQuantity();
            product.setStock(newStock);
            productRepository.save(product);

            inventoryLogRepository.save(InventoryLog.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .remainingStock(newStock)
                    .movementType(MovementType.SALE)
                    .movementDate(LocalDateTime.now())
                    .build());

            if (product.getTrackingType() == TrackingType.IMEI) {
                for (String imei : itemDTO.getImeiNumbers()) {
                    ProductImei imeiEntity = productImeiRepository
                            .findByImeiNumberAndStatus(imei, ImeiStatus.IN_STOCK)
                            .orElseThrow(() -> new RuntimeException(
                                    "IMEI not found or already sold: " + imei));
                    imeiEntity.setStatus(ImeiStatus.SOLD);
                    productImeiRepository.save(imeiEntity);
                }
            }
        }

        sale.setTotalAmount(total);
        saleRepository.save(sale);
    }

    @Override
    public List<SaleHistoryDTO> getAllSales() {
        return saleRepository.findAllOrderByDateDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleHistoryDTO> getSalesByDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to))
            throw new RuntimeException("From date cannot be after To date");
        return saleRepository.findByDateRange(from, to)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SaleHistoryDTO toDTO(Sale s) {
        return SaleHistoryDTO.builder()
                .id(s.getId())
                .saleDate(s.getSaleDate())
                .paymentMode(s.getPaymentMode())
                .totalAmount(s.getTotalAmount())
                .itemCount(s.getItems() != null ? s.getItems().size() : 0)
                .build();
    }
}