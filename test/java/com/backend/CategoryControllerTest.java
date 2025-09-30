package com.backend;

import com.backend.controller.CategoryController;
import com.backend.dto.CategoryDTO;
import com.backend.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    private MockMvc mockMvc;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        CategoryController categoryController = new CategoryController(categoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void getAllCategories_returnsList() throws Exception {
        List<CategoryDTO> categories = List.of(
                new CategoryDTO(UUID.randomUUID(), "Pants"),
                new CategoryDTO(UUID.randomUUID(), "Jeans")
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pants"));
    }

    @Test
    void createCategory_returnsCreatedCategory() throws Exception {
        CategoryDTO input = new CategoryDTO(null, "Dresses");
        CategoryDTO saved = new CategoryDTO(UUID.randomUUID(), "Dresses");

        when(categoryService.createCategory(any())).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Dresses\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dresses"));
    }

    @Test
    void deleteCategory_returnsTrue() throws Exception {
        UUID id = UUID.randomUUID();
        when(categoryService.deleteCategory(id)).thenReturn(true);

        mockMvc.perform(delete("/api/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void updateCategory_returnsUpdatedCategory() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryDTO updated = new CategoryDTO(id, "Updated");

        when(categoryService.updateCategory(eq(id), any())).thenReturn(updated);

        mockMvc.perform(put("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idCategory\":\"" + id + "\", \"name\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }
}