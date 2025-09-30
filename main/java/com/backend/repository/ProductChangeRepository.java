package com.backend.repository;

import com.backend.entity.ProductChange;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductChangeRepository extends CassandraRepository<ProductChange, UUID> {
    List<ProductChange> findByProductId(UUID productId);
}
