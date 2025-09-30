package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductOrderDTO {
    private UUID productId;
    private int quantity;
}
