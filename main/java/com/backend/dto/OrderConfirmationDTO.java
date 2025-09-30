package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderConfirmationDTO {
    private UUID orderId;
    private double totalPrice;
    private String message;
}
