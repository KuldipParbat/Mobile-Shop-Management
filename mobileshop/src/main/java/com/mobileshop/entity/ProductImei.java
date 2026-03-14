package com.mobileshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_imei")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImei {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imeiNumber;

    @Enumerated(EnumType.STRING)
    private ImeiStatus status;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}