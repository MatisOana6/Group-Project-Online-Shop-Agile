package com.backend.service;

import com.backend.dto.*;
import com.backend.entity.*;
import com.backend.mapper.OrderItemMapper;
import com.backend.mapper.OrderListingMapper;
import com.backend.mapper.OrderMapper;
import com.backend.neo4j.service.UserGraphService;
import com.backend.repository.OrderRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final OrderListingMapper orderListingMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserGraphService userGraphService;

    @Transactional("transactionManager")
    public OrderConfirmationDTO placeOrder(OrderRequestDTO request, UUID userId) {
        double totalPrice = 0.0;
        List<OrderItem> items = new ArrayList<>();

        for (ProductOrderDTO itemDTO : request.getProducts()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
            OrderItem orderItem = orderItemMapper.fromDTO(itemDTO, product);
            totalPrice += orderItem.getPrice();
            items.add(orderItem);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderMapper.fromRequestDTO(request, user, items, totalPrice);
        order.setStatus(calculateGlobalStatus(items));
        orderRepository.save(order);

        items.stream()
                .map(item -> item.getProduct().getSeller().getIdUser())
                .distinct()
                .forEach(sellerId -> {
                    String message = "New order received containing your product(s)";
                    System.out.println("Sending order notification to seller " + sellerId);
                    messagingTemplate.convertAndSend(
                            "/topic/seller/" + sellerId + "/notifications",
                            message
                    );
                });

        List<UUID> productIds = items.stream()
                .map(item -> item.getProduct().getIdProduct())
                .toList();

        try {
            userGraphService.addBoughtProducts(userId, productIds);
        } catch (Exception e) {
            System.err.println("Neo4j BOUGHT relation error: " + e.getMessage());
            e.printStackTrace();
        }


        return new OrderConfirmationDTO(order.getIdOrder(), totalPrice, "Order placed successfully.");
    }

    @Transactional
    public List<OrderListingDTO> getOrdersForSeller(UUID sellerId) {
        List<Order> orders = orderRepository.findOrdersByProductSellerId(sellerId);
        return orders.stream()
                .map(order -> orderListingMapper.toDTO(order, sellerId))
                .toList();
    }

    private OrderStatus calculateGlobalStatus(List<OrderItem> items) {
        Set<OrderItemStatus> statuses = items.stream()
                .map(OrderItem::getStatus)
                .collect(Collectors.toSet());

        if (statuses.size() == 1 && statuses.contains(OrderItemStatus.SHIPPED)) {
            return OrderStatus.SHIPPED;
        }

        if (statuses.size() == 1 && statuses.contains(OrderItemStatus.PENDING)) {
            return OrderStatus.PENDING;
        }

        if (statuses.size() == 1 && statuses.contains(OrderItemStatus.CANCELLED)) {
            return OrderStatus.CANCELLED;
        }

        if (statuses.contains(OrderItemStatus.SHIPPED) && statuses.contains(OrderItemStatus.PENDING)) {
            return OrderStatus.PARTIALLY_SHIPPED;
        }

        return OrderStatus.PENDING;
    }

    @Transactional
    public OrderProductSellerDTO updateOrderItemStatus(UUID orderId, UUID sellerId, UpdateOrderItemStatusDTO updateDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = order.getOrderItems().stream()
                .filter(i -> i.getIdItem().equals(updateDTO.getOrderItemId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("OrderItem not found in this order"));

        if (!item.getProduct().getSeller().getIdUser().equals(sellerId)) {
            throw new RuntimeException("Seller not authorized to update this product");
        }

        item.setStatus(updateDTO.getNewStatus());

        order.setStatus(calculateGlobalStatus(order.getOrderItems()));
        orderRepository.save(order);

        return orderItemMapper.toSellerDTO(item);
    }

    @Transactional
    public List<OrderDTO> getOrdersForBuyer(UUID buyerId) {
        List<Order> orders = orderRepository.findByUser_IdUser(buyerId);

        return orders.stream()
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .map(order -> {
                    OrderDTO orderDTO = orderMapper.toDTO(order);
                    orderDTO.getProducts().forEach(product -> {
                        product.setStatus(product.getStatus());
                    });
                    return orderDTO;
                })
                .toList();
    }

    @Transactional
    public List<SalesStatsDTO> getSalesStats(String category, String region, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDateTime end = (endDate != null)
                ? endDate.plusDays(1).atStartOfDay()
                : LocalDateTime.of(2100, 1, 1, 0, 0);

        if (category == null) {
            return orderRepository.getSalesStatsForAllCategories(region, start, end);
        } else {
            return orderRepository.getSalesStats(category, region, start, end);
        }
    }
}
