package com.mobileshop.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDTO {

    private LocalDate saleDate;

    private String paymentMode;

    private List<SaleItemDTO> items;

}