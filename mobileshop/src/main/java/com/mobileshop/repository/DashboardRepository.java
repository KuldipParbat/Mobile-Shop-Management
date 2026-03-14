package com.mobileshop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager em;

    public Double getTodaySales(LocalDate date) {
        // PostgreSQL: CAST instead of DATE()
        String sql = """
            SELECT COALESCE(SUM(total_amount), 0)
            FROM sales
            WHERE sale_date::date = :date
            """;
        Object result = em.createNativeQuery(sql)
                .setParameter("date", date)
                .getSingleResult();
        return result != null ? ((Number) result).doubleValue() : 0.0;
    }

    public Long getTotalProducts() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM products"
        ).getSingleResult();
        return ((Number) result).longValue();
    }

    public Long getTotalSuppliers() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM suppliers"
        ).getSingleResult();
        return ((Number) result).longValue();
    }

    public Long getLowStockCount() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM products WHERE stock <= 5"
        ).getSingleResult();
        return ((Number) result).longValue();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getWeeklySales(LocalDate from, LocalDate to) {
        // PostgreSQL: use ::date cast instead of DATE()
        String sql = """
            SELECT sale_date::date as sale_day,
                   COALESCE(SUM(total_amount), 0) as total
            FROM sales
            WHERE sale_date::date >= :from
              AND sale_date::date <= :to
            GROUP BY sale_date::date
            ORDER BY sale_day ASC
            """;
        return em.createNativeQuery(sql)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getLowStockProductList() {
        // PostgreSQL: LIMIT works same, no change needed
        String sql = """
            SELECT p.name, p.brand, p.category, p.stock
            FROM products p
            WHERE p.stock <= 5
            ORDER BY p.stock ASC
            LIMIT 7
            """;
        return em.createNativeQuery(sql).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getSalesByCategory() {
        String sql = """
            SELECT p.category,
                   COALESCE(SUM(si.quantity * si.selling_price), 0) as revenue
            FROM sale_items si
            JOIN products p ON si.product_id = p.id
            GROUP BY p.category
            ORDER BY revenue DESC
            LIMIT 5
            """;
        return em.createNativeQuery(sql).getResultList();
    }
}