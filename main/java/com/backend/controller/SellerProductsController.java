package com.backend.controller;

import com.backend.dto.ProductCreationWrapper;
import com.backend.dto.ProductDTO;
import com.backend.service.JwtService;
import com.backend.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/seller/products")
@CrossOrigin(origins = "http://frontend:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class SellerProductsController {
    private final ProductService productService;
    private final JwtService jwtService;

    @PutMapping()
    @PreAuthorize("hasAnyRole('SELLER')")
    public ResponseEntity<ProductDTO> updateProduct (@RequestBody ProductDTO productDTO) {
        ProductDTO dto = productService.updateProduct(productDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('SELLER')")
    public ResponseEntity<List<ProductDTO>> getAllSellerProducts(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        UUID sellerId = jwtService.extractId(token);
        List<ProductDTO> products = productService.getAllProductsForSeller(sellerId);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductCreationWrapper dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        UUID sellerId = jwtService.extractId(token);
        dto.setIdSeller(sellerId);
        ProductDTO savedProduct = productService.createProduct(dto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id, HttpServletRequest request) {
        UUID sellerId = jwtService.extractId(request.getHeader("Authorization"));
        Optional<ProductCreationWrapper> productOpt = productService.getProductById(id);
        if (productOpt.isPresent() && productOpt.get().getIdSeller().equals(sellerId)) {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
