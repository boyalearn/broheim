package com.broheim.websocket.core.endpoint.server;

import com.broheim.websocket.core.endpoint.EndpointConfig;
import com.broheim.websocket.core.endpoint.EndpointCreator;
import com.broheim.websocket.core.endpoint.ServerWebSocketEndpoint;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Setter
@Getter
public class WebSocketServer extends EndpointConfig {

    private static Map<String, EventPublisher> completePublisher = new ConcurrentHashMap<>();

    private EndpointCreator endpointCreator = new EndpointCreator();

    public static EventPublisher findEventPublisher(String path) {
        return completePublisher.get(path);
    }

    public void service(String path, EventPublisher eventPublisher) {
        completePublisher.put(path, eventPublisher);
        try {
            Class<ServerWebSocketEndpoint> endpoint = endpointCreator.createEndpoint(path);
            Class.forName(endpoint.getName());
        } catch (Exception e) {
            log.error("exception happen", e);
        }
    }
}
