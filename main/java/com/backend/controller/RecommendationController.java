package com.backend.controller;

import com.backend.dto.ProductRecommendationDTO;
import com.backend.service.RecommendationService;
import com.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final JwtService jwtService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT','SELLER')")
    public ResponseEntity<List<ProductRecommendationDTO>> getRecommendations(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        UUID userId = jwtService.extractId(token);
        List<ProductRecommendationDTO> recommendations = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }
}

