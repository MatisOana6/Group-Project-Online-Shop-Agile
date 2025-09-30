package com.backend.neo4j.node;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
import java.util.UUID;

@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNode {

    @Id
    private UUID id;

    @Relationship(type = "VIEWED", direction = Relationship.Direction.OUTGOING)
    private Set<ProductNode> viewedProducts;

    @Relationship(type = "BOUGHT")
    private Set<ProductNode> boughtProducts;

    @Relationship(type = "PREFERS")
    private Set<CategoryNode> preferredCategories;

}
