package com.backend.mapper;

import com.backend.dto.ProductCreationWrapper;
import com.backend.dto.ProductDTO;
import com.backend.dto.ProductRecommendationDTO;
import com.backend.entity.Category;
import com.backend.entity.Product;
import com.backend.entity.Review;
import com.backend.entity.User;
import com.backend.neo4j.node.ProductNode;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO toDTO(Product product) {
        double averageRating = 0;
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            averageRating = product.getReviews().stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
        }

        return new ProductDTO(
                product.getIdProduct(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getImage(),
                product.getDescription(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                averageRating
        );
    }

    public Product toProduct(ProductCreationWrapper dto, Category category, User seller) {
        return Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .image(dto.getImage())
                .description(dto.getDescription())
                .category(category)
                .seller(seller)
                .build();
    }

    public ProductRecommendationDTO toRecommendationDTO(ProductNode node) {
        return new ProductRecommendationDTO(
                node.getId(),
                node.getName(),
                node.getPrice(),
                node.getCategory() != null ? node.getCategory().getName() : "Unknown",
                node.getImage()
        );
    }


}
