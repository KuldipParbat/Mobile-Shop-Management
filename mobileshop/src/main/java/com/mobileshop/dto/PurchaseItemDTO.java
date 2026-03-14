package com.mobileshop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemDTO {

    private Long productId;

    private Integer quantity;

    private Double purchasePrice;

    private List<String> imeiNumbers;

}