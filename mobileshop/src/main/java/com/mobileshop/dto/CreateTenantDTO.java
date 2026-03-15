package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTenantDTO {
    private String shopName;
    private String ownerName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String gstNumber;
}