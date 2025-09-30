package com.backend.mapper;

import com.backend.dto.OrderDTO;
import com.backend.dto.OrderProductDTO;
import com.backend.dto.OrderRequestDTO;
import com.backend.entity.Order;
import com.backend.entity.OrderItem;
import com.backend.entity.OrderStatus;
import com.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderProductMapper orderProductMapper;
  
    public Order fromRequestDTO(OrderRequestDTO dto, User user, List<OrderItem> items, double total) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderItems(items);
        order.setAddress(dto.getDelivery().getAddress());
        order.setRegion(dto.getDelivery().getRegion());
        order.setPhoneNumber(dto.getDelivery().getPhone());
        order.setStatus(OrderStatus.PENDING);
        order.setDate(LocalDateTime.now());
        order.setTotal(total);

        for (OrderItem item : items) {
            item.setOrder(order);
        }

        return order;
    }

    public OrderDTO toDTO(Order order) {
        List<OrderProductDTO> products = order.getOrderItems().stream()
                .map(orderProductMapper::toDTO)
                .toList();

        return new OrderDTO(
                order.getTotal(),
                order.getStatus().name(),
                order.getDate(),
                products
        );
    }
}
