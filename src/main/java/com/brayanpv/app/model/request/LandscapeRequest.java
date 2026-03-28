package com.brayanpv.app.model.request;

import org.springframework.http.codec.multipart.FilePart;


public record LandscapeRequest(FilePart file,
                               String title,
                               String description,
                               Double latitude,
                               Double longitude) {
}
