package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesStatsDTO {
    private String category;
    private String region;
    private long totalOrders;
    private double totalRevenue;
}
