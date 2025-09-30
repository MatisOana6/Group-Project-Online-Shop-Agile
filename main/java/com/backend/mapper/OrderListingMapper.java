package com.backend.mapper;

import com.backend.dto.OrderListingDTO;
import com.backend.dto.OrderProductDTO;
import com.backend.dto.OrderProductSellerDTO;
import com.backend.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderListingMapper {
    public OrderListingDTO toDTO(Order order, UUID sellerId) {
        List<OrderProductSellerDTO> products = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getSeller().getIdUser().equals(sellerId))
                .map(item -> new OrderProductSellerDTO(
                        item.getIdItem(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        item.getStatus().name()
                ))
                .toList();

        return new OrderListingDTO(
                order.getIdOrder(),
                order.getTotal(),
                order.getAddress(),
                order.getRegion(),
                order.getPhoneNumber(),
                products
        );
    }
}
