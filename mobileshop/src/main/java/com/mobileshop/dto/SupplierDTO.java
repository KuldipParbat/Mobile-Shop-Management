package com.mobileshop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {

    private Long id;

    private String name;

    private String phone;

    private String address;

    private String gstNumber;

}