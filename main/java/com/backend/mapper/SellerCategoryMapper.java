package com.backend.mapper;

import com.backend.dto.SellerCategoryDTO;
import com.backend.entity.SellerCategory;
import org.springframework.stereotype.Component;

@Component
public class SellerCategoryMapper {
    public SellerCategoryDTO toDTO(SellerCategory sc) {
        return new SellerCategoryDTO(
                sc.getSeller().getIdUser(),
                sc.getCategory().getIdCategory(),
                sc.getCategory().getName()
        );
    }
}
