package com.backend.repository;

import com.backend.entity.Product;
import com.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByCategory_IdCategory(UUID idCategory);
    List<Product> findByPriceBetweenAndCategory_IdCategory(Double minPrice, Double maxPrice, UUID id);
    List<Product> findAllBySeller(User seller);
}
