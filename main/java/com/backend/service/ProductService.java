package com.backend.service;

import com.backend.dto.ProductCreationWrapper;
import com.backend.dto.ProductDTO;
import com.backend.dto.ReviewDTO;
import com.backend.entity.*;
import com.backend.mapper.ProductMapper;
import com.backend.neo4j.service.UserGraphService;
import com.backend.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final ProductChangeRepository productChangeRepository;
    private final SellerCategoryRepository sellerCategoryRepository;
    private final ReviewRepository reviewRepository;
    private final JwtService jwtService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserGraphService userGraphService;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductDtoById(UUID id, String token) {
        Optional<ProductDTO> result = productRepository.findById(id).map(productMapper::toDTO);

        try {
            UUID userId = jwtService.extractId(token);
            userGraphService.addViewedProduct(userId, id);
        } catch (Exception e) {
            System.err.println("Neo4j error in VIEWED relation: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }



    public Optional<ProductCreationWrapper> getProductById(UUID id) {
        Optional<ProductCreationWrapper> result = productRepository.findById(id).map(product -> {
            ProductCreationWrapper wrapper = new ProductCreationWrapper();
            wrapper.setName(product.getName());
            wrapper.setDescription(product.getDescription());
            wrapper.setPrice(product.getPrice());
            wrapper.setQuantity(product.getQuantity());
            wrapper.setImage(product.getImage());
            wrapper.setIdCategory(product.getCategory().getIdCategory());
            wrapper.setIdSeller(product.getSeller().getIdUser());
            return wrapper;
        });

        try {
            String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            UUID userId = jwtService.extractId(token);
            userGraphService.addViewedProduct(userId, id);
        } catch (Exception e) {
            System.out.println("Could not add VIEWED relation: " + e.getMessage());
        }

        return result;
    }



    public List<ProductDTO> filterProducts(Double minPrice, Double maxPrice, String categoryId, Double minRating, Double maxRating) {
        List<Product> products;

        if (minPrice != null && maxPrice != null && categoryId != null) {
            UUID id = UUID.fromString(categoryId);
            products = productRepository.findByPriceBetweenAndCategory_IdCategory(minPrice, maxPrice, id);
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        } else if (categoryId != null) {
            UUID id = UUID.fromString(categoryId);
            products = productRepository.findByCategory_IdCategory(id);
        } else {
            products = productRepository.findAll();
        }

        if (minRating != null && maxRating != null) {
            products = products.stream()
                    .filter(product -> {
                        double avgRating = product.getReviews().stream()
                                .mapToDouble(review -> review.getRating())
                                .average()
                                .orElse(0.0);
                        return avgRating >= minRating && avgRating <= maxRating;
                    })
                    .collect(Collectors.toList());
        }

        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(ProductCreationWrapper dto) {
        Category category = categoryRepository.findById(dto.getIdCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        User seller = userRepository.findById(dto.getIdSeller())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        if (!seller.getRole().name().equals("SELLER")) {
            throw new RuntimeException("User is not a seller");
        }
        Product product = productMapper.toProduct(dto, category, seller);
        product.setImage(dto.getImage());
        boolean allowed = sellerCategoryRepository.existsBySeller_IdUserAndCategory_IdCategory(
                dto.getIdSeller(), dto.getIdCategory()
        );
        if (!allowed) {
            throw new IllegalArgumentException("Seller not allowed to add product to this category");
        }
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Transactional
    public boolean deleteProduct(UUID id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if(productOptional.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findById(productDTO.getIdProduct());

        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product existingProduct = productOptional.get();

        if (existingProduct.getPrice() != productDTO.getPrice()) {
            productChangeRepository.save(new ProductChange(
                    existingProduct.getIdProduct(),
                    Instant.now(),
                    "price",
                    BigDecimal.valueOf(existingProduct.getPrice()),
                    BigDecimal.valueOf(productDTO.getPrice())
            ));
            existingProduct.setPrice(productDTO.getPrice());
        }

        if (existingProduct.getQuantity() != productDTO.getQuantity()) {
            productChangeRepository.save(new ProductChange(
                    existingProduct.getIdProduct(),
                    Instant.now(),
                    "stock",
                    BigDecimal.valueOf(existingProduct.getQuantity()),
                    BigDecimal.valueOf(productDTO.getQuantity())
            ));
            existingProduct.setQuantity(productDTO.getQuantity());
        }

        Category category = categoryRepository.findByName(productDTO.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setImage(productDTO.getImage());
        existingProduct.setCategory(category);

        productRepository.save(existingProduct);

        return productMapper.toDTO(existingProduct);
    }

    public List<ProductDTO> getAllProductsForSeller(UUID sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return productRepository.findAllBySeller(seller).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductChange> getProductHistory(UUID productId) {
        return productChangeRepository.findByProductId(productId);
    }

    @Transactional("transactionManager")
    public void addReview(UUID productId, ReviewDTO dto, String token) {
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        UUID userId = jwtService.extractId(token);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyReviewed = reviewRepository.existsByUser_IdUserAndProduct_IdProduct(userId, productId);
        if (alreadyReviewed) {
            throw new IllegalStateException("User already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        reviewRepository.save(review);

        UUID sellerId = product.getSeller().getIdUser();
        String message = "New feedback submitted on product: " + product.getName();

        messagingTemplate.convertAndSend(
                "/topic/seller/" + sellerId + "/notifications",
                message
        );

        try {
            userGraphService.updatePreferredCategory(userId, product.getCategory().getIdCategory());
        } catch (Exception e) {
            System.err.println("Neo4j error: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
