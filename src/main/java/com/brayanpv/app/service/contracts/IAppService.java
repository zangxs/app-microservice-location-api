package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeDetailResponse;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.model.response.NearbyLandscapeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAppService {

    Mono<LandscapeResponse> uploadFile(LandscapeRequest request);
    Flux<NearbyLandscapeResponse> getNearby(Double lat, Double lng, Integer radius, String ip);
    Mono<LandscapeDetailResponse> getLandscape(String id);
    Mono<Boolean> hasLiked(String landscapeId, String userId);

}
