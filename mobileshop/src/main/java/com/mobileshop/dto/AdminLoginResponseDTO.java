package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminLoginResponseDTO {
    private String token;
    private String name;
    private String email;
    private String role;
}