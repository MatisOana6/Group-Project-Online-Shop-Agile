package com.backend.mapper;

import com.backend.dto.OrderProductDTO;
import com.backend.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderProductMapper {

    public OrderProductDTO toDTO(OrderItem orderItem) {
        double unitPrice = orderItem.getQuantity() != 0
                ? orderItem.getPrice() / orderItem.getQuantity()
                : 0.0;

        return new OrderProductDTO(
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                unitPrice,
                orderItem.getStatus().name()
        );
    }
}
