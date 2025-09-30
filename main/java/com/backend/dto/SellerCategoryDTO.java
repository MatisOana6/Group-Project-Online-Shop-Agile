package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerCategoryDTO {
    private UUID sellerId;
    private UUID categoryId;
    private String categoryName;
}
