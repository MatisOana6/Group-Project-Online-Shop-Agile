package com.backend.repository;

import com.backend.dto.SalesStatsDTO;
import com.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems i WHERE i.product.seller.idUser = :sellerId")
    List<Order> findOrdersByProductSellerId(@Param("sellerId") UUID sellerId);

    List<Order> findByUser_IdUser(UUID buyerId);

    @Query("""
    SELECT new com.backend.dto.SalesStatsDTO(
        p.category.name,
        o.region,
        COUNT(DISTINCT o.idOrder),
        SUM(oi.price)
    )
    FROM Order o
    JOIN o.orderItems oi
    JOIN oi.product p
    WHERE (:category IS NULL OR p.category.name = :category)
      AND (:region IS NULL OR o.region = :region)
      AND (CAST(:startDate AS timestamp) IS NULL OR o.date >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR o.date <= :endDate)
    GROUP BY p.category.name, o.region
    """)
    List<SalesStatsDTO> getSalesStats(
            @Param("category") String category,
            @Param("region") String region,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT new com.backend.dto.SalesStatsDTO(
        p.category.name,
        o.region,
        COUNT(DISTINCT o.idOrder),
        SUM(oi.price)
    )
    FROM Order o
    JOIN o.orderItems oi
    JOIN oi.product p
    WHERE (:region IS NULL OR o.region = :region)
      AND (CAST(:startDate AS timestamp) IS NULL OR o.date >= :startDate)
      AND (CAST(:endDate AS timestamp) IS NULL OR o.date <= :endDate)
    GROUP BY p.category.name, o.region
    """)
    List<SalesStatsDTO> getSalesStatsForAllCategories(
            @Param("region") String region,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
