package com.backend.service;

import com.backend.dto.ProductRecommendationDTO;
import com.backend.mapper.ProductMapper;
import com.backend.neo4j.node.ProductNode;
import com.backend.neo4j.repository.ProductNodeRepository;
import com.backend.neo4j.repository.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RecommendationService {

    private final ProductNodeRepository productRepo;
    private final UserNodeRepository userRepo;
    private final ProductMapper productMapper;

    public List<ProductRecommendationDTO> getRecommendations(UUID userId) {
        Set<ProductNode> recommendations = new LinkedHashSet<>();

        if (!userRepo.existsById(userId)) {
            return productRepo.recommendPopularProducts().stream()
                    .map(productMapper::toRecommendationDTO)
                    .toList();
        }

        recommendations.addAll(productRepo.recommendBasedOnViews(userId));
        recommendations.addAll(productRepo.recommendBySimilarUsers(userId));
        recommendations.addAll(productRepo.recommendByPreferredCategories(userId));

        if (recommendations.size() < 5) {
            recommendations.addAll(productRepo.recommendPopularProducts());
        }

        return recommendations.stream()
                .limit(10)
                .map(productMapper::toRecommendationDTO)
                .toList();
    }
  
    private List<ProductRecommendationDTO> map(List<ProductNode> products) {
        return products.stream()
                .map(p -> new ProductRecommendationDTO(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getCategory() != null ? p.getCategory().getName() : "Unknown",
                        p.getImage()
                ))
                .toList();


    }
}