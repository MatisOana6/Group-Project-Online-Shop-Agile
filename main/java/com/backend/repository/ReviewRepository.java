package com.backend.repository;

import com.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProduct_IdProduct(UUID productId);
    boolean existsByUser_IdUserAndProduct_IdProduct(UUID userId, UUID productId);
}
