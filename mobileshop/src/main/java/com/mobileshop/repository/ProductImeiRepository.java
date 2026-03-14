package com.mobileshop.repository;

import com.mobileshop.entity.ImeiStatus;
import com.mobileshop.entity.ProductImei;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImeiRepository extends JpaRepository<ProductImei, Long> {
    Optional<ProductImei> findByImeiNumberAndStatus(String imeiNumber, ImeiStatus status);
    List<ProductImei> findByProductIdAndStatus(Long productId, ImeiStatus status);
}