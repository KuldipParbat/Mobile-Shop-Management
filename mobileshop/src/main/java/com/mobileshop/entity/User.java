package com.mobileshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "public")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    private boolean active = true;
}