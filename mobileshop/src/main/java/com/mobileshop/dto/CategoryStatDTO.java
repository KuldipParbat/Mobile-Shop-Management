package com.mobileshop.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryStatDTO {
    private String name;
    private Double pct;
    private String color;
}