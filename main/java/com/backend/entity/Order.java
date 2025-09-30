package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "\"ORDER\"")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {

    @Id
    @Column(name = "id_order")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idOrder;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total")
    private double total;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "address")
    private String address;

    @Column(name = "region")
    private String region;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

}
