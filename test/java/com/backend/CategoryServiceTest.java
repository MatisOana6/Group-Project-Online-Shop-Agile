package com.backend;

import com.backend.dto.CategoryDTO;
import com.backend.entity.Category;
import com.backend.mapper.CategoryMapper;
import com.backend.repository.CategoryRepository;
import com.backend.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_shouldReturnMappedList() {
        Category category = new Category();
        category.setIdCategory(UUID.randomUUID());
        category.setName("Pants");

        CategoryDTO dto = new CategoryDTO(category.getIdCategory(), "Pants");

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(dto);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Pants", result.get(0).getName());
    }

    @Test
    void createCategory_shouldSaveAndReturnDTO() {
        CategoryDTO inputDTO = new CategoryDTO(null, "Pants");
        Category entity = Category.builder()
                .idCategory(UUID.randomUUID())
                .name("Pants")
                .build();
        CategoryDTO outputDTO = new CategoryDTO(entity.getIdCategory(), "Pants");

        when(categoryMapper.toCategory(inputDTO)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.toDTO(entity)).thenReturn(outputDTO);

        CategoryDTO result = categoryService.createCategory(inputDTO);

        assertNotNull(result);
        assertEquals("Pants", result.getName());
        verify(categoryRepository).save(entity);
    }

    @Test
    void deleteCategory_shouldReturnTrue_whenCategoryExists() {
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setIdCategory(id);
        category.setName("Delete me");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        boolean result = categoryService.deleteCategory(id);

        assertTrue(result);
        verify(categoryRepository).deleteById(id);
    }

    @Test
    void deleteCategory_shouldReturnFalse_whenCategoryNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = categoryService.deleteCategory(id);

        assertFalse(result);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void updateCategory_shouldUpdateAndReturnDTO_whenCategoryExists() {
        UUID id = UUID.randomUUID();
        Category existing = new Category();
        existing.setIdCategory(id);
        existing.setName("Old");

        CategoryDTO inputDTO = new CategoryDTO(id, "New");
        CategoryDTO updatedDTO = new CategoryDTO(id, "New");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);
        when(categoryMapper.toDTO(existing)).thenReturn(updatedDTO);

        CategoryDTO result = categoryService.updateCategory(id, inputDTO);

        assertEquals("New", result.getName());
        verify(categoryRepository).save(existing);
    }

}