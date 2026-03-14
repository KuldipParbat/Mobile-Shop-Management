package com.mobileshop.repository;

import com.mobileshop.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT DISTINCT p FROM Purchase p " +
            "LEFT JOIN FETCH p.items " +
            "LEFT JOIN FETCH p.supplier " +
            "ORDER BY p.purchaseDate DESC")
    List<Purchase> findAllWithItems();

    @Query("SELECT DISTINCT p FROM Purchase p " +
            "LEFT JOIN FETCH p.items " +
            "LEFT JOIN FETCH p.supplier " +
            "WHERE p.purchaseDate BETWEEN :from AND :to " +
            "ORDER BY p.purchaseDate DESC")
    List<Purchase> findByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}