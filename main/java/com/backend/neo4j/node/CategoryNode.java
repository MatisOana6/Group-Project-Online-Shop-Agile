package com.backend.neo4j.node;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryNode {

    @Id
    private UUID id;
    private String name;
}

