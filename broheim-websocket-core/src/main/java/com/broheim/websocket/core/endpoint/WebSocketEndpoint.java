package com.broheim.websocket.core.endpoint;

import com.broheim.websocket.core.event.EventPublisher;

import javax.websocket.Session;
import java.util.concurrent.atomic.AtomicInteger;

public interface WebSocketEndpoint {

    AtomicInteger sendId();

    Session getSession();

    EventPublisher getEventPublisher();
}
