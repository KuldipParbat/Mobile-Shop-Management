package com.mobileshop.dto;

import com.mobileshop.entity.TrackingType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    private String name;

    private String brand;

    private String model;

    private String category;

    private Double purchasePrice;

    private Double sellingPrice;

    private Integer stock;

    private TrackingType trackingType;

}