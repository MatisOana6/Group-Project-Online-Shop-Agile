package com.backend;

import com.backend.controller.ReviewController;
import com.backend.dto.ReviewDTO;
import com.backend.entity.Product;
import com.backend.entity.Review;
import com.backend.entity.User;
import com.backend.service.JwtService;
import com.backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest {

    private MockMvc mockMvc;

    private ReviewService reviewService;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        jwtService = mock(JwtService.class);

        ReviewController reviewController = new ReviewController(reviewService, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void getReviewsByProductId_returnsList() throws Exception {
        UUID productId = UUID.randomUUID();
        ReviewDTO reviewDTO = new ReviewDTO();
        when(reviewService.getReviewsByProductId(productId)).thenReturn(List.of(reviewDTO));

        mockMvc.perform(get("/api/reviews/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void replyToFeedback_whenValid_returnsOk() throws Exception {
        UUID feedbackId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        String token = "Bearer valid-token";
        String reply = "Thanks for the feedback!";

        when(jwtService.extractId("Bearer valid-token")).thenReturn(sellerId);

        doNothing().when(reviewService).replyToFeedback(feedbackId, sellerId, reply);

        mockMvc.perform(put("/api/reviews/feedback/{feedbackId}", feedbackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reply\":\"" + reply + "\"}")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void replyToFeedback_whenSecurityException_returnsForbidden() throws Exception {
        UUID feedbackId = UUID.randomUUID();
        String reply = "Unauthorized reply";

        when(jwtService.extractId(anyString())).thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(put("/api/reviews/feedback/{feedbackId}", feedbackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reply\":\"" + reply + "\"}")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void replyToFeedback_whenRuntimeException_returnsBadRequest() throws Exception {
        UUID feedbackId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        String reply = "Runtime error";
        String token = "Bearer valid-token";

        when(jwtService.extractId(token)).thenReturn(sellerId);
        doThrow(new RuntimeException("Some error")).when(reviewService).replyToFeedback(feedbackId, sellerId, reply);

        mockMvc.perform(put("/api/reviews/feedback/{feedbackId}", feedbackId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reply\":\"" + reply + "\"}")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Some error"));
    }
}
