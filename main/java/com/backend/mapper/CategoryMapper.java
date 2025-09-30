package com.backend.mapper;

import com.backend.dto.CategoryDTO;
import com.backend.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getIdCategory(),
                category.getName()
        );
    }

    public Category toCategory(CategoryDTO categoryDTO) {
        return Category.builder()
                .idCategory(categoryDTO.getIdCategory())
                .name(categoryDTO.getName())
                .build();
    }
}
