package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderProductSellerDTO {
    private UUID orderItemId;
    private String name;
    private int quantity;
    private double price;
    private String status;
}
