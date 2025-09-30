package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "REVIEW")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {

    @Id
    @Column(name = "id_review")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idReview;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @Column(name = "rating")
    private double rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "reply")
    private String reply;

}
