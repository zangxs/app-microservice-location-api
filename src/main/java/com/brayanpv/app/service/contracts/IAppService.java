package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeDetailResponse;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.model.response.NearbyLandscapeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAppService {

    Mono<LandscapeResponse> uploadFile(LandscapeRequest request);
    Flux<NearbyLandscapeResponse> getNearby(Double lat, Double lng, Integer radius, String ip, String baseUrl);
    Mono<LandscapeDetailResponse> getLandscape(String id, String baseUrl);
    Mono<Boolean> hasLiked(String landscapeId, String userId);
    Flux<LandscapeDetailResponse> getMyLandscapes(String userId, String baseUrl);
}
