package com.broheim.websocket.core.config;

import com.broheim.websocket.core.event.DefaultEventPublisher;
import com.broheim.websocket.core.event.EventPublisher;
import com.broheim.websocket.core.listener.DefaultClientEventListener;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ClientConfig {

    private EventPublisher publisher;

    private Protocol protocol = new SimpleProtocol();

    private List<EventListener> eventListeners = new ArrayList<>();

    public EventPublisher buildClientPublisher() {
        EventPublisher returnPublisher = new DefaultEventPublisher();
        if (null != this.publisher) {
            returnPublisher = publisher;
        }
        if (eventListeners.isEmpty()) {
            eventListeners.add(new DefaultClientEventListener());
        }
        returnPublisher.addListeners(eventListeners);
        return returnPublisher;
    }
}
