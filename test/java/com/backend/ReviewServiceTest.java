package com.backend;

import com.backend.dto.ReviewDTO;
import com.backend.entity.Product;
import com.backend.entity.Review;
import com.backend.entity.User;
import com.backend.mapper.ReviewMapper;
import com.backend.repository.ReviewRepository;
import com.backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private UUID productId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productId = UUID.randomUUID();
    }

    @Test
    void getReviewsByProductId() {
        Review review = new Review();
        review.setIdReview(UUID.randomUUID());
        List<Review> reviews = List.of(review);

        when(reviewRepository.findByProduct_IdProduct(productId)).thenReturn(reviews);
        when(reviewMapper.toDto(review)).thenReturn(new ReviewDTO());

        List<ReviewDTO> result = reviewService.getReviewsByProductId(productId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByProduct_IdProduct(productId);
    }

    @Test
    void replyToFeedback_whenSellerIsNull() {
        UUID feedbackId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        String reply = "Thank you for your feedback!";
        Review review = new Review();
        review.setIdReview(feedbackId);
        review.setProduct(new Product());

        when(reviewRepository.findById(feedbackId)).thenReturn(java.util.Optional.of(review));

        assertThrows(SecurityException.class, () -> {
            reviewService.replyToFeedback(feedbackId, sellerId, reply);
        });
    }


    @Test
    void replyToFeedback_whenSuccess() {
        UUID feedbackId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        String reply = "Thank you for your feedback!";
        Product product = new Product();
        User seller = new User();
        seller.setIdUser(sellerId);
        product.setSeller(seller);
        Review review = new Review();
        review.setIdReview(feedbackId);
        review.setProduct(product);
        when(reviewRepository.findById(feedbackId)).thenReturn(java.util.Optional.of(review));

        reviewService.replyToFeedback(feedbackId, sellerId, reply);

        assertEquals(reply, review.getReply());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void replyToFeedback_whenReviewNotFound() {
        UUID feedbackId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        String reply = "Test";

        when(reviewRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            reviewService.replyToFeedback(feedbackId, sellerId, reply);
        });
    }



}

