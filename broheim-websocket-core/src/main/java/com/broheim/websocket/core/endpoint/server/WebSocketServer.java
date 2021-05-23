package com.broheim.websocket.core.endpoint.server;

import com.broheim.websocket.core.endpoint.ServerWebSocketEndpoint;
import com.broheim.websocket.core.endpoint.EndpointConfig;
import com.broheim.websocket.core.listener.AsyncMessageSendListener;
import com.broheim.websocket.core.listener.RequestResponseMessageSendListener;
import com.broheim.websocket.core.listener.ServerHeartbeatListener;
import com.broheim.websocket.core.listener.SyncMessageSendListener;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class WebSocketServer extends EndpointConfig {

    private static EventPublisher completePublisher;

    public static EventPublisher findEventPublisher(Class<? extends ServerWebSocketEndpoint> aClass) {
        return completePublisher;
    }

    public void start() {
        if (null == this.listeners) {
            this.listeners = new ArrayList<>();
            this.listeners.add(new ServerHeartbeatListener());
            this.listeners.add(new SyncMessageSendListener());
            this.listeners.add(new AsyncMessageSendListener());
            this.listeners.add(new RequestResponseMessageSendListener());
        }
        defaultConfig();
        completePublisher = this.eventPublisher;
    }


}
