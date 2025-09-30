package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "SELLER_CATEGORY")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerCategory {

    @EmbeddedId
    private SellerCategoryId idSellerCategory;


    @ManyToOne
    @MapsId("idSeller")
    @JoinColumn(name = "id_seller", referencedColumnName = "id_user")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "id_category", referencedColumnName = "id_category")
    @MapsId("idCategory")
    private Category category;

}
