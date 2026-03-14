package com.mobileshop.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklySalesDTO {
    private List<String> labels;
    private List<Double> sales;
    private List<Double> profits;
}