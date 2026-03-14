package com.mobileshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private Integer remainingStock;

    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    private LocalDateTime movementDate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}