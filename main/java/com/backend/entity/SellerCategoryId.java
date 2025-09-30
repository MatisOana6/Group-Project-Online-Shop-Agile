package com.backend.entity;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerCategoryId implements Serializable {
    private UUID idSeller;
    private UUID idCategory;
}
