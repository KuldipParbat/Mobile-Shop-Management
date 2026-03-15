package com.mobileshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants", schema = "public")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "owner_name")
    private String ownerName;

    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;

    @Column(name = "gst_number")
    private String gstNumber;

    private boolean active = true;
}