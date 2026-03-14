package com.mobileshop.report.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepository {

    @PersistenceContext
    private EntityManager em;

    // ── helper: build date filter clause ──
    private String dateFilter(String from, String to, String alias, boolean hasWhere) {
        String connector = hasWhere ? " AND " : " WHERE ";
        if (from != null && to != null) {
            return connector + alias + ".sale_date::date BETWEEN '"
                    + from + "' AND '" + to + "'";
        }
        return connector + alias + ".sale_date::date >= CURRENT_DATE - INTERVAL '30 days'";
    }

    private String purchaseDateFilter(String from, String to, boolean hasWhere) {
        String connector = hasWhere ? " AND " : " WHERE ";
        if (from != null && to != null) {
            return connector + "pu.purchase_date BETWEEN '"
                    + from + "' AND '" + to + "'";
        }
        return connector + "pu.purchase_date >= CURRENT_DATE - INTERVAL '30 days'";
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getDailySalesReport(String from, String to) {
        String sql = "SELECT s.sale_date::date, COALESCE(SUM(s.total_amount), 0) " +
                "FROM sales s" +
                dateFilter(from, to, "s", false) +
                " GROUP BY s.sale_date::date " +
                "ORDER BY s.sale_date::date DESC";
        return em.createNativeQuery(sql).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getProductSalesReport() {
        String sql = """
                SELECT p.name,
                       COALESCE(SUM(si.quantity), 0) as total_sold,
                       COALESCE(SUM(si.quantity * si.selling_price), 0) as revenue
                FROM sale_items si
                JOIN products p ON si.product_id = p.id
                GROUP BY p.name
                ORDER BY total_sold DESC
                """;
        return em.createNativeQuery(sql).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getWeeklySummaryReport() {
        String sql = """
                SELECT
                    TO_CHAR(DATE_TRUNC('week', sale_date), 'DD Mon YYYY') as week_start,
                    COALESCE(SUM(total_amount), 0) as total_sales,
                    COALESCE(SUM(total_amount * 0.2), 0) as total_profit,
                    COUNT(*) as total_orders
                FROM sales
                WHERE sale_date >= CURRENT_DATE - INTERVAL '12 weeks'
                GROUP BY DATE_TRUNC('week', sale_date)
                ORDER BY DATE_TRUNC('week', sale_date) DESC
                """;
        return em.createNativeQuery(sql).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getPaymentModeReport(String from, String to) {
        String sql = "SELECT s.payment_mode, COUNT(*) as total_orders, " +
                "COALESCE(SUM(s.total_amount), 0) as total_amount " +
                "FROM sales s" +
                dateFilter(from, to, "s", false) +
                " GROUP BY s.payment_mode " +
                "ORDER BY total_amount DESC";
        return em.createNativeQuery(sql).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getProfitLossReport(String from, String to) {
        String sql = "SELECT s.sale_date::date, " +
                "COALESCE(SUM(s.total_amount), 0) as revenue, " +
                "COALESCE(SUM(si.quantity * p.purchase_price), 0) as cost " +
                "FROM sales s " +
                "JOIN sale_items si ON si.sale_id = s.id " +
                "JOIN products p ON si.product_id = p.id" +
                dateFilter(from, to, "s", false) +
                " GROUP BY s.sale_date::date " +
                "ORDER BY s.sale_date::date DESC";
        return em.createNativeQuery(sql).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getSupplierPurchaseReport(String from, String to) {
        String sql = "SELECT sup.name, " +
                "COUNT(DISTINCT pu.id) as total_purchases, " +
                "COALESCE(SUM(pi.quantity * pi.purchase_price), 0) as total_amount " +
                "FROM purchases pu " +
                "JOIN suppliers sup ON pu.supplier_id = sup.id " +
                "JOIN purchase_items pi ON pi.purchase_id = pu.id" +
                purchaseDateFilter(from, to, false) +
                " GROUP BY sup.name " +
                "ORDER BY total_amount DESC";
        return em.createNativeQuery(sql).getResultList();
    }
}