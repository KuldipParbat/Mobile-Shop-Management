package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateStaffDTO {
    private String name;
    private String email;
    private String password;
}