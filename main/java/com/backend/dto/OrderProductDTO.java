package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProductDTO {
    private String name;
    private int quantity;
    private double price;
    private String status;
}
