package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.service.contracts.IAppService;
import com.brayanpv.app.service.contracts.IRabbitMQService;
import com.brayanpv.app.service.contracts.IS3Service;
import com.brayanspv.library.model.events.LandscapeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Log4j2
@RequiredArgsConstructor
public class AppService implements IAppService {

    private final IS3Service s3Service;
    private final ILandscapeRepository landscapeRepository;
    private final IRabbitMQService rabbitMQService;

    @Override
    public Mono<LandscapeResponse> uploadFile(LandscapeRequest request) {
        log.info("request received: {}", request.toString());
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");

            return s3Service.uploadFile(request.file())
                    .flatMap(imageUrl -> {
                        LandscapeEntity entity = LandscapeEntity.builder()
                                .userId(Long.parseLong(userId))
                                .title(request.title())
                                .description(request.description())
                                .latitude(request.latitude())
                                .longitude(request.longitude())
                                .imageUrl(imageUrl)
                                .status("PENDING")
                                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                                .build();

                        return landscapeRepository.save(entity);
                    })
                    .map(saved -> {
                        log.info("Landscape saved with id: {}", saved.getId());
                        log.info("Landscape saved with id: {}", saved.getId());
                        LandscapeEvent event = new LandscapeEvent(
                                saved.getId(),
                                userId,
                                ctx.get("email"),
                                saved.getTitle(),
                                saved.getDescription(),
                                saved.getLatitude(),
                                saved.getLongitude(),
                                saved.getImageUrl()
                        );
                        return Mono.just(event);
                    }).flatMap(eventMono -> eventMono.flatMap(event ->
                            rabbitMQService.publishLandscape(event)
                                    .thenReturn(new LandscapeResponse(event.landscapeId(), "PENDING"))
                    ));
        });
    }
}
