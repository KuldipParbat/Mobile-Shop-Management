package com.mobileshop.repository;

import com.mobileshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // ✅ use exact field name from entity (tenantId maps to tenant_id column)
    List<User> findByTenantId(String tenantId);
    long countByTenantId(String tenantId);
}