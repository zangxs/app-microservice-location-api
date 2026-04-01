package com.brayanpv.app.component.messaging.contracts;

import com.brayanspv.library.model.events.LandscapeStatusEvent;

public interface ILandscapeStatusConsumer {
    void consume(LandscapeStatusEvent event);
}
