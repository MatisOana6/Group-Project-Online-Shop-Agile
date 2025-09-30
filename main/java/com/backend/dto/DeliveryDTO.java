package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryDTO {
    private String address;
    private String region;
    private String phone;
}
