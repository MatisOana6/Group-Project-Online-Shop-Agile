package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderListingDTO {
    private UUID orderId;
    private double total;
    private String address;
    private String region;
    private String phone;
    private List<OrderProductSellerDTO> products;
}
