package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductDTO {
    private UUID idProduct;
    private String name;
    private double price;
    private int quantity;
    private String image;
    private String description;
    private String categoryName;
    private double averageRating;

}
