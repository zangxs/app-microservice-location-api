package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.LocationRequest;
import com.brayanpv.app.model.response.LocationResponse;
import com.brayanpv.app.service.contracts.IAppService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class AppService implements IAppService {
    @Override
    public Mono<LocationResponse> uploadLocation(LocationRequest locationRequest) {
        return null;
    }
}
