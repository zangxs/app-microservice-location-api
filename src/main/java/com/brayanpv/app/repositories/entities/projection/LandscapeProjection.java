package com.brayanpv.app.repositories.entities.projection;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Data
public class LandscapeProjection {
    private UUID id;
    private Long userId;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    @Column("image_url")
    private String imageUrl;
    private String status;
    private Double distance;
}
