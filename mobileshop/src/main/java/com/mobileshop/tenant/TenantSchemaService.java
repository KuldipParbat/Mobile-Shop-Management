package com.mobileshop.tenant;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
public class TenantSchemaService {

    private final DataSource dataSource;

    public TenantSchemaService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTenantSchema(String tenantId) {
        String schema = "tenant_" + tenantId;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // create schema
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema);

            // switch to new schema
            stmt.execute("SET search_path TO " + schema);

            // create all tables
            stmt.execute("""
    CREATE TABLE IF NOT EXISTS products (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255),
        brand VARCHAR(255),
        model VARCHAR(255),
        category VARCHAR(255),
        purchase_price DOUBLE PRECISION,
        selling_price DOUBLE PRECISION,
        stock INTEGER DEFAULT 0,
        tracking_type VARCHAR(50)
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS suppliers (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255),
        phone VARCHAR(20),
        address TEXT,
        gst_number VARCHAR(20)
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS sales (
        id BIGSERIAL PRIMARY KEY,
        sale_date DATE,
        payment_mode VARCHAR(50),
        total_amount DOUBLE PRECISION
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS sale_items (
        id BIGSERIAL PRIMARY KEY,
        sale_id BIGINT REFERENCES sales(id),
        product_id BIGINT REFERENCES products(id),
        quantity INTEGER,
        selling_price DOUBLE PRECISION,
        discount DOUBLE PRECISION DEFAULT 0
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS purchases (
        id BIGSERIAL PRIMARY KEY,
        supplier_id BIGINT REFERENCES suppliers(id),
        invoice_number VARCHAR(100),
        purchase_date DATE
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS purchase_items (
        id BIGSERIAL PRIMARY KEY,
        purchase_id BIGINT REFERENCES purchases(id),
        product_id BIGINT REFERENCES products(id),
        quantity INTEGER,
        purchase_price DOUBLE PRECISION
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS product_imei (
        id BIGSERIAL PRIMARY KEY,
        product_id BIGINT REFERENCES products(id),
        imei_number VARCHAR(20),
        status VARCHAR(20)
    )""");

            stmt.execute("""
    CREATE TABLE IF NOT EXISTS inventory_logs (
        id BIGSERIAL PRIMARY KEY,
        product_id BIGINT REFERENCES products(id),
        quantity INTEGER,
        remaining_stock INTEGER,
        movement_type VARCHAR(20),
        movement_date TIMESTAMP
    )""");

            // reset search path
            stmt.execute("SET search_path TO public");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create tenant schema: " + e.getMessage(), e);
        }
    }
}