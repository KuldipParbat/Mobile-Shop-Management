package com.mobileshop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItemDTO {

    private Long productId;

    private Integer quantity;

    private Double sellingPrice;

    private Double discount;

    private List<String> imeiNumbers;

}