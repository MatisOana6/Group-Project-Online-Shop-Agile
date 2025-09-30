package com.backend.controller;

import com.backend.dto.ProductCreationWrapper;
import com.backend.dto.ProductDTO;
import com.backend.dto.ReviewDTO;
import com.backend.entity.ProductChange;
import com.backend.service.ProductService;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://frontend:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT','SELLER')")
    public List<ProductDTO> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'SELLER')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        return productService.getProductDtoById(id, token)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT','SELLER')")
    public ResponseEntity<List<ProductDTO>> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ) {
        return ResponseEntity.ok(productService.filterProducts(minPrice, maxPrice, category, minRating, maxRating));
    }


    @PostMapping
    public ResponseEntity<ProductDTO> create (@RequestBody ProductCreationWrapper dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('SELLER')")
    public ResponseEntity<Boolean> deleteProduct (@PathVariable("id") UUID id) {
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }

    @GetMapping("/history/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<ProductChange> getProductHistory(@PathVariable UUID id) {
        return productService.getProductHistory(id);
    }


    @PostMapping("/{productId}/reviews")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> addReview(@PathVariable UUID productId, @RequestBody @Valid ReviewDTO dto, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            productService.addReview(productId, dto, token);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }


}
