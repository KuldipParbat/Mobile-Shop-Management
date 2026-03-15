package com.mobileshop.controller;

import com.mobileshop.dto.*;
import com.mobileshop.entity.*;
import com.mobileshop.repository.*;
import com.mobileshop.security.JwtService;
import com.mobileshop.service.EmailService;
import com.mobileshop.tenant.TenantSchemaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRepository      adminRepository;
    private final TenantRepository     tenantRepository;
    private final UserRepository       userRepository;
    private final TenantSchemaService  schemaService;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final EmailService         emailService;

    @PersistenceContext
    private EntityManager em;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    // ── Admin Login ──
    @PostMapping("/login")
    public AdminLoginResponseDTO adminLogin(
            @RequestBody AdminLoginDTO dto) {

        // auto-create admin if not exists
        if (!adminRepository.existsByEmail(adminEmail)) {
            adminRepository.save(Admin.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .name("Super Admin")
                    .build());
        }

        Admin admin = adminRepository.findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid admin credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword()))
            throw new RuntimeException("Invalid admin credentials");

        return AdminLoginResponseDTO.builder()
                .token(jwtService.generateAdminToken(admin))
                .name(admin.getName())
                .email(admin.getEmail())
                .role("ADMIN")
                .build();
    }

    // ── Create Tenant ──
    @PostMapping("/tenants")
    @Transactional
    public String createTenant(@RequestBody CreateTenantDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already registered");

        // generate unique tenantId
        String base = dto.getShopName()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        String tenantId = base;
        int suffix = 1;
        while (tenantRepository.existsByTenantId(tenantId)) {
            tenantId = base + "_" + suffix++;
        }

        Tenant tenant = Tenant.builder()
                .tenantId(tenantId)
                .shopName(dto.getShopName())
                .ownerName(dto.getOwnerName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .gstNumber(dto.getGstNumber())
                .active(true)
                .build();
        tenantRepository.save(tenant);

        // create owner user
        String tempPassword = dto.getPassword() != null
                ? dto.getPassword()
                : generateTempPassword();

        User owner = User.builder()
                .name(dto.getOwnerName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(UserRole.OWNER)
                .tenantId(tenantId)
                .active(true)
                .build();
        userRepository.save(owner);

        // create schema
        schemaService.createTenantSchema(tenantId);

        // send welcome email
        emailService.sendWelcomeEmail(
                dto.getEmail(), dto.getOwnerName(),
                dto.getShopName(), tempPassword);

        return "Tenant created. TenantId: " + tenantId;
    }

    // ── Add Staff ──
    @PostMapping("/tenants/{tenantId}/staff")
    public String createStaff(
            @PathVariable String tenantId,
            @RequestBody CreateStaffDTO dto) {

        tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already registered");

        String tempPassword = generateTempPassword();

        User staff = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(UserRole.STAFF)
                .tenantId(tenantId)
                .active(true)
                .build();
        userRepository.save(staff);

        // get shop name
        String shopName = tenantRepository
                .findByTenantId(tenantId)
                .map(Tenant::getShopName)
                .orElse("Mobile Shop");

        emailService.sendWelcomeEmail(
                dto.getEmail(), dto.getName(), shopName, tempPassword);

        return "Staff created successfully";
    }

    // ── Get All Tenants with Stats ──
    @GetMapping("/tenants")
    public List<TenantStatsDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(t -> {
                    long userCount = userRepository
                            .countByTenantId(t.getTenantId());

                    long productCount = 0;
                    long salesCount   = 0;
                    try {
                        String schema = "tenant_" + t.getTenantId();
                        productCount = ((Number) em.createNativeQuery(
                                        "SELECT COUNT(*) FROM " + schema + ".products")
                                .getSingleResult()).longValue();
                        salesCount = ((Number) em.createNativeQuery(
                                        "SELECT COUNT(*) FROM " + schema + ".sales")
                                .getSingleResult()).longValue();
                    } catch (Exception ignored) {}

                    return TenantStatsDTO.builder()
                            .id(t.getId())
                            .tenantId(t.getTenantId())
                            .shopName(t.getShopName())
                            .ownerName(t.getOwnerName())
                            .email(t.getEmail())
                            .phone(t.getPhone())
                            .city(t.getCity())
                            .state(t.getState())
                            .gstNumber(t.getGstNumber())
                            .active(t.isActive())
                            .totalUsers(userCount)
                            .totalProducts(productCount)
                            .totalSales(salesCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── Get Users by Tenant ──
    @GetMapping("/tenants/{tenantId}/users")
    public List<User> getTenantUsers(@PathVariable String tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    // ── Toggle Tenant Active ──
    @PutMapping("/tenants/{tenantId}/toggle")
    public String toggleTenant(@PathVariable String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setActive(!tenant.isActive());
        tenantRepository.save(tenant);
        return tenant.isActive() ? "activated" : "deactivated";
    }

    // ── Toggle User Active ──
    @PutMapping("/users/{userId}/toggle")
    public String toggleUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return user.isActive() ? "activated" : "deactivated";
    }

    // ── Admin Reset User Password ──
    @PostMapping("/users/{userId}/reset-password")
    public String adminResetPassword(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newPassword = generateTempPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        String shopName = tenantRepository
                .findByTenantId(user.getTenantId())
                .map(Tenant::getShopName)
                .orElse("Mobile Shop");

        emailService.sendWelcomeEmail(
                user.getEmail(), user.getName(), shopName, newPassword);

        return "Password reset. New password sent to " + user.getEmail();
    }

    // ── Helper ──
    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#$";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(
                    (int)(Math.random() * chars.length())));
        }
        return sb.toString();
    }
}