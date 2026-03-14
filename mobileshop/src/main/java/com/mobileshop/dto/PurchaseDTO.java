package com.mobileshop.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO {

    private Long supplierId;

    private String invoiceNumber;

    private LocalDate purchaseDate;

    private List<PurchaseItemDTO> items;

}