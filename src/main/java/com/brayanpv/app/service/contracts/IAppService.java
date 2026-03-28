package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeResponse;
import reactor.core.publisher.Mono;

public interface IAppService {

    Mono<LandscapeResponse> uploadFile(LandscapeRequest request);
}
