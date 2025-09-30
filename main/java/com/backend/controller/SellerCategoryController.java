package com.backend.controller;

import com.backend.dto.AssignCategoryRequest;
import com.backend.dto.SellerCategoryDTO;
import com.backend.service.JwtService;
import com.backend.service.SellerCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seller-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SellerCategoryController {
    private final SellerCategoryService sellerCategoryService;
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignCategory(@RequestBody AssignCategoryRequest request) {
        sellerCategoryService.assignCategoryToSeller(request.getSellerId(), request.getCategoryId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sellerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<List<SellerCategoryDTO>> getAssignedCategories(@PathVariable UUID sellerId) {
        return ResponseEntity.ok(sellerCategoryService.getCategoriesBySeller(sellerId));
    }

    @DeleteMapping("/{sellerId}/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeCategory(@PathVariable UUID sellerId, @PathVariable UUID categoryId) {
        sellerCategoryService.removeCategoryFromSeller(sellerId, categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<SellerCategoryDTO>> getAssignedCategories(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        UUID sellerId = jwtService.extractId(token);
        List<SellerCategoryDTO> assignedCategories = sellerCategoryService.getCategoriesBySeller(sellerId);
        return ResponseEntity.ok(assignedCategories);
    }


}
