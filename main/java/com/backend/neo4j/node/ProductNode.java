package com.backend.neo4j.node;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
import java.util.UUID;

@Node("Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductNode {

    @Id
    private UUID id;
    private String name;
    private double price;
    private String image;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private CategoryNode category;

    @Relationship(type = "VIEWED", direction = Relationship.Direction.OUTGOING)
    private Set<ProductNode> viewedProducts;


}

