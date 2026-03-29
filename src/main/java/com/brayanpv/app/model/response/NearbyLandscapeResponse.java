package com.brayanpv.app.model.response;

public record NearbyLandscapeResponse( String id,
                                       String title,
                                       String description,
                                       Double latitude,
                                       Double longitude,
                                       String imageUrl,
                                       Double distance) {
}
