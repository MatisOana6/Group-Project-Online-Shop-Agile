package com.backend.mapper;

import com.backend.dto.ReviewDTO;
import com.backend.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewDTO toDto(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setIdReview(review.getIdReview());
        dto.setUserId(review.getUser().getIdUser());
        dto.setUsername(review.getUser().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setReply(review.getReply());
        return dto;
    }
}
