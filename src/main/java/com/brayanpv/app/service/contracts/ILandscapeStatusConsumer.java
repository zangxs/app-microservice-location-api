package com.brayanpv.app.service.contracts;

import com.brayanspv.library.model.events.LandscapeStatusEvent;

public interface ILandscapeStatusConsumer {
    void consume(LandscapeStatusEvent event);
}
