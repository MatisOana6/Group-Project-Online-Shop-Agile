package com.backend.dto;

import com.backend.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateOrderStatusDTO {
    private OrderStatus status;
}
