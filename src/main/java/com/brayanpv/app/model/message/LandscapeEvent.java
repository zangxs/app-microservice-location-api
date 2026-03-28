package com.brayanpv.app.model.message;

public record LandscapeEvent(
        String landscapeId,
        String userId,
        String email,
        String title,
        String description,
        Double latitude,
        Double longitude,
        String imageUrl
) {}
