package com.backend.mapper;

import com.backend.dto.OrderProductSellerDTO;
import com.backend.dto.ProductOrderDTO;
import com.backend.entity.OrderItem;
import com.backend.entity.OrderItemStatus;
import com.backend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    public OrderItem fromDTO(ProductOrderDTO dto, Product product) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice() * dto.getQuantity());
        item.setStatus(OrderItemStatus.PENDING);
        return item;
    }

    public OrderProductSellerDTO toSellerDTO(OrderItem item) {
        return new OrderProductSellerDTO(
                item.getIdItem(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getStatus().name()
        );
    }
}
