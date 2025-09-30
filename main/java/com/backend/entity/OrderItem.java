package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "ORDER_ITEM")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItem {

    @Id
    @Column(name = "id_item")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idItem;

    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderItemStatus status;
}
