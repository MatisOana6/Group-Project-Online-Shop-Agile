package com.backend.controller;

import com.backend.dto.ReviewDTO;
import com.backend.service.JwtService;
import com.backend.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.backend.service.JwtService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtService jwtService;

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT','SELLER')")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @PutMapping("/feedback/{feedbackId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> replyToFeedback(@PathVariable UUID feedbackId,
                                             @RequestBody Map<String, String> body,
                                             HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            UUID sellerId = jwtService.extractId(token);
            String reply = body.get("reply");
            reviewService.replyToFeedback(feedbackId, sellerId, reply);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
