package com.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductCreationWrapper {
    private String name;
    private double price;
    private int quantity;
    private String image;
    private String description;
    private UUID idCategory;
    private UUID idSeller;
}
