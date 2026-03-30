package com.brayanpv.app.model.response;

public record LandscapeDetailResponse(
        String id,
        String title,
        String description,
        Double latitude,
        Double longitude,
        String imageUrl,
        String status
) {}
