package com.broheim.client.service;

import com.broheim.websocket.core.endpoint.ClientWebSocketEndpoint;
import com.broheim.websocket.core.endpoint.client.WebSocketClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.websocket.DeploymentException;
import java.io.IOException;

@Service
public class WebSocketService {

    public WebSocketClient webSocketClient;

    @PostConstruct
    public void init() throws IOException, DeploymentException {
        String url = "ws://localhost:8000/ws";
        WebSocketClient client = new WebSocketClient();
        client.connect(url, ClientWebSocketEndpoint.class);
        this.webSocketClient = client;
    }

    public String echo(String context) throws Exception {
        return (String)this.webSocketClient.sendMessage(context);
    }
}
