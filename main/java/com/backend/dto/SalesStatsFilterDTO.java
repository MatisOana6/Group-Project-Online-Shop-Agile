package com.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesStatsFilterDTO {
    private String category;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
}
