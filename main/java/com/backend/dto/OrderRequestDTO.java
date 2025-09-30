package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderRequestDTO {
    private List<ProductOrderDTO> products;
    private DeliveryDTO delivery;
}
