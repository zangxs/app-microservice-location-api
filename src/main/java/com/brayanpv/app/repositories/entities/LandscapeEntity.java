package com.brayanpv.app.repositories.entities;

import org.springframework.data.relational.core.mapping.Table;

import java.sql.Date;

@Table(name = "landscape")
public class LandscapeEntity {

    private String id;
    private Long userId;
    private String title;
    private String description;
    private Float latitude;
    private Float longitude;
    private String imageUrl;
    private String status;
    private Date createdAt;
    private Date updatedAt;

}
