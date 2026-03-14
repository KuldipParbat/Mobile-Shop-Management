package com.mobileshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String brand;

    private String model;

    private String category;

    private Double purchasePrice;

    private Double sellingPrice;

    private Integer stock;

    @Enumerated(EnumType.STRING)
    private TrackingType trackingType;
}