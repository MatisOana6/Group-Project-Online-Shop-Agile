package com.backend.dto;

import com.backend.entity.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateOrderItemStatusDTO {
    private UUID orderItemId;
    private OrderItemStatus newStatus;
}
