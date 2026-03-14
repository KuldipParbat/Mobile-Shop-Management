package com.mobileshop.repository;

import com.mobileshop.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s ORDER BY s.saleDate DESC")
    List<Sale> findAllOrderByDateDesc();

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :from AND :to ORDER BY s.saleDate DESC")
    List<Sale> findByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}