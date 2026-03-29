package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.ExifResult;
import com.brayanpv.app.service.contracts.IExifService;
import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExifService implements IExifService {
    @Override
    public Mono<ExifResult> extractCoordinates(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> Mono.fromCallable(() -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    Metadata metadata = ImageMetadataReader.readMetadata(
                            new ByteArrayInputStream(bytes)
                    );

                    GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

                    if (gpsDirectory == null || gpsDirectory.getGeoLocation() == null) {
                        log.info("No GPS metadata found in image");
                        return new ExifResult(bytes, null, null);
                    }

                    GeoLocation location = gpsDirectory.getGeoLocation();
                    log.info("GPS coordinates extracted: lat={}, lng={}",
                            location.getLatitude(), location.getLongitude());

                    return new ExifResult(bytes, location.getLatitude(), location.getLongitude());

                }).subscribeOn(Schedulers.boundedElastic()));
    }
}
