package com.backend.controller;

import com.backend.dto.*;
import com.backend.service.JwtService;
import com.backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {
    private final OrderService orderService;
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderConfirmationDTO> placeOrder(@RequestBody OrderRequestDTO request, @RequestHeader("Authorization") String token) {
        UUID userId = jwtService.extractId(token);
        OrderConfirmationDTO confirmation = orderService.placeOrder(request, userId);

        return ResponseEntity.ok(confirmation);
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<OrderListingDTO>> getSellerOrders(@RequestHeader("Authorization") String authHeader) {
        UUID sellerId = jwtService.extractId(authHeader);
        List<OrderListingDTO> orders = orderService.getOrdersForSeller(sellerId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/seller/{orderId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<OrderProductSellerDTO> updateOrderItemStatus(@PathVariable UUID orderId, @RequestBody UpdateOrderItemStatusDTO updateDTO, @RequestHeader("Authorization") String authHeader)
    {
        UUID sellerId = jwtService.extractId(authHeader);
        OrderProductSellerDTO updatedItem = orderService.updateOrderItemStatus(orderId, sellerId, updateDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<OrderDTO>> getBuyerOrders(@RequestHeader("Authorization") String authHeader) {
        UUID buyerId = jwtService.extractId(authHeader);
        List<OrderDTO> orders = orderService.getOrdersForBuyer(buyerId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SalesStatsDTO>> getSalesStats(@RequestBody SalesStatsFilterDTO filter) {
        if (filter.getCategory() == null || filter.getCategory().isEmpty()) {
            filter.setCategory(null);
        }

        List<SalesStatsDTO> stats = orderService.getSalesStats(
                filter.getCategory(),
                filter.getRegion(),
                filter.getStartDate(),
                filter.getEndDate()
        );
        return ResponseEntity.ok(stats);
    }


}
