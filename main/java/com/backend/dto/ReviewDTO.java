package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private UUID idReview;
    private UUID userId;
    private String username;
    private double rating;
    private String comment;
    private String reply;
}
