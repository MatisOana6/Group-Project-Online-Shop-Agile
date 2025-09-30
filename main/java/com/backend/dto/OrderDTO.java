package com.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {
    private double total;
    private String status;
    private LocalDateTime date;
    private List<OrderProductDTO> products;
}
