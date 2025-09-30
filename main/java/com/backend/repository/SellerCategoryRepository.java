package com.backend.repository;

import com.backend.entity.SellerCategory;
import com.backend.entity.SellerCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SellerCategoryRepository extends JpaRepository<SellerCategory, SellerCategoryId> {
    List<SellerCategory> findAllBySeller_IdUser(UUID sellerId);
    void deleteBySeller_IdUserAndCategory_IdCategory(UUID sellerId, UUID categoryId);

    boolean existsBySeller_IdUserAndCategory_IdCategory(UUID idSeller, UUID idCategory);
}
