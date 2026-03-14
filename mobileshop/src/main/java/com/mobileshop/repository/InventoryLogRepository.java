package com.mobileshop.repository;

import com.mobileshop.entity.InventoryLog;
import com.mobileshop.entity.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByProductId(Long productId);
    List<InventoryLog> findByProductIdOrderByMovementDateDesc(Long productId);
    boolean existsByProductIdAndMovementType(Long productId, MovementType movementType);


}