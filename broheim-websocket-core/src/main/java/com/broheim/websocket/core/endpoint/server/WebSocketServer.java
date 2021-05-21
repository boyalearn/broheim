package com.broheim.websocket.core.endpoint.server;

import com.broheim.websocket.core.endpoint.AbstractWebSocketEndpoint;
import com.broheim.websocket.core.listener.AsyncMessageSendListener;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.listener.MessageReceiveListener;
import com.broheim.websocket.core.listener.RequestResponseMessageListener;
import com.broheim.websocket.core.listener.ServerHeartbeatListener;
import com.broheim.websocket.core.listener.SyncMessageSendListener;
import com.broheim.websocket.core.publisher.DefaultEventPublisher;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class WebSocketServer {

    private static EventPublisher completePublisher;

    private List<EventListener> eventListeners;

    private EventPublisher eventPublisher;

    public void start() {
        if (null == this.eventListeners) {
            this.eventListeners = new ArrayList<>();
            this.eventListeners.add(new ServerHeartbeatListener());
            this.eventListeners.add(new MessageReceiveListener());
            this.eventListeners.add(new SyncMessageSendListener());
            this.eventListeners.add(new AsyncMessageSendListener());
            this.eventListeners.add(new RequestResponseMessageListener());
        }

        if (null == this.eventPublisher) {
            this.eventPublisher = new DefaultEventPublisher(this.eventListeners);
        }
        completePublisher = this.eventPublisher;
    }

    public static EventPublisher findEventPublisher(Class<? extends AbstractWebSocketEndpoint> aClass) {
        return completePublisher;
    }
}
