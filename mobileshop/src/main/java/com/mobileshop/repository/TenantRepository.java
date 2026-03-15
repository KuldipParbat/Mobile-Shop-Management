package com.mobileshop.repository;

import com.mobileshop.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByTenantId(String tenantId);
    boolean existsByTenantId(String tenantId);
}