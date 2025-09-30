package com.backend.neo4j.repository;

import com.backend.neo4j.node.CategoryNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryNodeRepository extends Neo4jRepository<CategoryNode, UUID> {
}
