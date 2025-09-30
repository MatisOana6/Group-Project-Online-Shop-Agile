package com.backend.mapper;

import com.backend.dto.ProductRecommendationDTO;
import com.backend.neo4j.node.ProductNode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductRecommendationMapper {

    public ProductRecommendationDTO toDTO(ProductNode node) {
        return new ProductRecommendationDTO(
                node.getId(),
                node.getName(),
                node.getPrice(),
                node.getCategory() != null ? node.getCategory().getName() : "Unknown",
                node.getImage()
        );
    }

    public List<ProductRecommendationDTO> toDTOList(List<ProductNode> nodes) {
        return nodes.stream().map(this::toDTO).collect(Collectors.toList());
    }
}

