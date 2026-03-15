package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponseDTO {
    private String token;
    private String name;
    private String email;
    private String role;
    private String tenantId;
    private String shopName;
    private String shopAddress;
    private String shopPhone;
}