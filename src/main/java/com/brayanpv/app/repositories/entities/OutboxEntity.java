package com.brayanpv.app.repositories.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Table(name = "outbox")
public class OutboxEntity {
    private UUID id;
    @Column("aggregate_id")
    private UUID aggregateId;
    @Column("event_type")
    private String eventType;
    private String payload;
    private String status;
    private int retries;
    @Column("max_retries")
    private int maxRetries;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("processed_at")
    private LocalDateTime processedAt;

}
