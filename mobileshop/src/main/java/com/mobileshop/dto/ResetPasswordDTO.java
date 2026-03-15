package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ResetPasswordDTO {
    private String token;
    private String newPassword;
    private String confirmPassword;
}