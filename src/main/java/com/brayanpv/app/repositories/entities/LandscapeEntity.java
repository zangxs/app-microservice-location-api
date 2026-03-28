package com.brayanpv.app.repositories.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table(name = "landscape")
@Data
@Builder
public class LandscapeEntity {

    @Id
    private String id;
    @Column("user_id")
    private Long userId;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    @Column("image_url")
    private String imageUrl;
    private String status;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;

}
