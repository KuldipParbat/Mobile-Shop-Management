package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdminLoginDTO {
    private String email;
    private String password;
}