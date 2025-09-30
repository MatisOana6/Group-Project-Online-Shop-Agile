package com.backend.neo4j.service;

import com.backend.neo4j.node.CategoryNode;
import com.backend.neo4j.node.ProductNode;
import com.backend.neo4j.repository.CategoryNodeRepository;
import com.backend.neo4j.repository.ProductNodeRepository;
import com.backend.neo4j.repository.UserNodeRepository;

import com.backend.neo4j.node.UserNode;


import com.backend.repository.CategoryRepository;
import com.backend.entity.Category;
import com.backend.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGraphService {
    private final UserNodeRepository userRepo;
    private final ProductNodeRepository productRepo;
    private final CategoryNodeRepository categoryRepo;

    private final CategoryRepository jpaCategoryRepo;
    private final ProductRepository jpaProductRepo;


    @Transactional("neo4jTransactionManager")
    public void addBoughtProducts(UUID userId, List<UUID> productIds) {
        List<ProductNode> existingNodes = productRepo.findAllById(productIds);
        Set<UUID> existingIds = existingNodes.stream()
                .map(ProductNode::getId)
                .collect(Collectors.toSet());

        List<UUID> missingIds = productIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        List<ProductNode> createdNodes = missingIds.stream()
                .map(id -> jpaProductRepo.findById(id).map(product -> {
                    UUID categoryId = product.getCategory().getIdCategory();
                    String categoryName = product.getCategory().getName();

                    CategoryNode categoryNode = categoryRepo.findById(categoryId)
                            .orElseGet(() -> categoryRepo.save(
                                    CategoryNode.builder()
                                            .id(categoryId)
                                            .name(categoryName)
                                            .build()
                            ));

                    return ProductNode.builder()
                            .id(product.getIdProduct())
                            .name(product.getName())
                            .price(product.getPrice())
                            .image(product.getImage())
                            .category(categoryNode)
                            .build();
                }).orElse(null))
                .filter(Objects::nonNull)
                .map(productRepo::save)
                .toList();

        Set<ProductNode> allProducts = new HashSet<>();
        allProducts.addAll(existingNodes);
        allProducts.addAll(createdNodes);

        UserNode user = userRepo.findById(userId).orElseGet(() ->
                userRepo.save(UserNode.builder()
                        .id(userId)
                        .boughtProducts(new HashSet<>())
                        .preferredCategories(new HashSet<>())
                        .build()));

        if (user.getBoughtProducts() == null) {
            user.setBoughtProducts(new HashSet<>());
        }

        user.getBoughtProducts().addAll(allProducts);
        userRepo.save(user);
    }



    @Transactional("neo4jTransactionManager")
    public void updatePreferredCategory(UUID userId, UUID categoryId) {
        CategoryNode category = categoryRepo.findById(categoryId)
                .orElseGet(() -> {
                    String name = jpaCategoryRepo.findById(categoryId)
                            .map(Category::getName)
                            .orElse("Unknown");

                    System.out.println("Creating missing category node in Neo4j: " + name);

                    return categoryRepo.save(CategoryNode.builder()
                            .id(categoryId)
                            .name(name)
                            .build());
                });

        UserNode user = userRepo.findById(userId).orElse(
                UserNode.builder()
                        .id(userId)
                        .boughtProducts(new HashSet<>())
                        .preferredCategories(new HashSet<>())
                        .build()
        );

        if (user.getPreferredCategories() == null)
            user.setPreferredCategories(new HashSet<>());

        user.getPreferredCategories().add(category);
        userRepo.save(user);
    }


    @Transactional("neo4jTransactionManager")
    public void createUserNode(UUID userId) {
        if (userRepo.existsById(userId)) return;

        UserNode userNode = UserNode.builder()
                .id(userId)
                .boughtProducts(new HashSet<>())
                .preferredCategories(new HashSet<>())
                .build();

        userRepo.save(userNode);
    }

    @Transactional("neo4jTransactionManager")
    public void addViewedProduct(UUID userId, UUID productId) {
        ProductNode productNode = productRepo.findById(productId).orElseGet(() ->
                jpaProductRepo.findById(productId).map(product -> {
                    UUID categoryId = product.getCategory().getIdCategory();
                    String categoryName = product.getCategory().getName();

                    CategoryNode categoryNode = categoryRepo.findById(categoryId)
                            .orElseGet(() -> categoryRepo.save(
                                    CategoryNode.builder()
                                            .id(categoryId)
                                            .name(categoryName)
                                            .build()
                            ));

                    return productRepo.save(ProductNode.builder()
                            .id(product.getIdProduct())
                            .name(product.getName())
                            .price(product.getPrice())
                            .image(product.getImage())
                            .category(categoryNode)
                            .build());
                }).orElse(null)
        );

        if (productNode == null) return;

        UserNode user = userRepo.findById(userId).orElseGet(() ->
                userRepo.save(UserNode.builder()
                        .id(userId)
                        .viewedProducts(new HashSet<>())
                        .preferredCategories(new HashSet<>())
                        .build())
        );

        if (user.getViewedProducts() == null) {
            user.setViewedProducts(new HashSet<>());
        }

        if (!user.getViewedProducts().contains(productNode)) {
            user.getViewedProducts().add(productNode);
            userRepo.save(user);
        }
    }

}