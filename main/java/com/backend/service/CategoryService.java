package com.backend.service;

import com.backend.dto.CategoryDTO;
import com.backend.entity.Category;
import com.backend.mapper.CategoryMapper;
import com.backend.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toCategory(categoryDTO);
        categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    @Transactional
    public boolean deleteCategory(UUID categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            categoryRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }

    @Transactional
    public CategoryDTO updateCategory(UUID categoryId, CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Category category = new Category();
        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
            category.setName(categoryDTO.getName());
            categoryRepository.save(category);
        }
        return categoryMapper.toDTO(category);
    }
}
