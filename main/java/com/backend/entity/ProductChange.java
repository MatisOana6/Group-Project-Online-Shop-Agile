package com.backend.entity;

import org.springframework.data.cassandra.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("product_changes")
public class ProductChange {

    @PrimaryKeyColumn(name = "product_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID productId;

    @PrimaryKeyColumn(name = "changed_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant changedAt;

    private String fieldChanged;
    private BigDecimal oldValue;
    private BigDecimal newValue;
}