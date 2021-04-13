package com.broheim.websocket.core.configurator;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class HttpConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        sec.getUserProperties().put(HandshakeRequest.class.getName(), request);
        sec.getUserProperties().put(HandshakeResponse.class.getName(), response);
    }
}
