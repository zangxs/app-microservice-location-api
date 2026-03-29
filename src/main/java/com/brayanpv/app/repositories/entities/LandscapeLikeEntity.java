package com.brayanpv.app.repositories.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Table("landscape_like")
public class LandscapeLikeEntity {
    @Id
    private UUID id;
    @Column("landscape_id")
    private UUID landscapeId;
    @Column("user_id")
    private Long userId;
    @Column("created_at")
    private LocalDateTime createdAt;
}
