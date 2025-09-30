package com.backend.neo4j.repository;

import com.backend.neo4j.node.ProductNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.UUID;

public interface ProductNodeRepository extends Neo4jRepository<ProductNode, UUID> {

    @Query("""
    MATCH (u:User {id: $userId})-[:BOUGHT]->(:Product)<-[:BOUGHT]-(similar:User)-[:BOUGHT]->(rec:Product)-[:BELONGS_TO]->(c:Category)
    RETURN DISTINCT rec, c
    ORDER BY rec.name LIMIT 20
    """)
    List<ProductNode> recommendBySimilarUsers(UUID userId);

    @Query("""
    MATCH (u:User {id: $userId})-[:PREFERS]->(c:Category)<-[:BELONGS_TO]-(p:Product)
    RETURN DISTINCT p, c
    ORDER BY p.name LIMIT 20
    """)
    List<ProductNode> recommendByPreferredCategories(UUID userId);

    @Query("""
    MATCH (p:Product)<-[r:BOUGHT]-()
    MATCH (p)-[:BELONGS_TO]->(c:Category)
    WITH p, c, count(r) AS purchases
    RETURN p, c
    ORDER BY purchases DESC LIMIT 20
    """)
    List<ProductNode> recommendPopularProducts();

    @Query("""
    MATCH (u:User {id: $userId})-[:VIEWED]->(p:Product)<-[:VIEWED]-(similar:User)-[:BOUGHT]->(rec:Product)-[:BELONGS_TO]->(c:Category)
    RETURN DISTINCT rec, c
    ORDER BY rec.name LIMIT 20
    """)
    List<ProductNode> recommendBasedOnViews(UUID userId);
}