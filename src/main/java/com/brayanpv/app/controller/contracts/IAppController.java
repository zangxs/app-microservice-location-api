package com.brayanpv.app.controller.contracts;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IAppController {

    Mono<ResponseEntity<ApiResponse>> uploadLocation(LandscapeRequest request);
}
