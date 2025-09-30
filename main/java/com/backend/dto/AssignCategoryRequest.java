package com.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignCategoryRequest {
    private UUID sellerId;
    private UUID categoryId;
}
