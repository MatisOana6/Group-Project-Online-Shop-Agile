package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendationDTO {

    private UUID id;
    private String name;
    private double price;
    private String categoryName;
    private String image;
}
