package com.broheim.websocket.client;

import com.broheim.websocket.core.client.WebSocketClient;
import com.broheim.websocket.core.config.ClientConfig;

public class ClientTest {
    public static void main(String[] args) throws InterruptedException {
        String url = "ws://localhost:8080/ws";
        new WebSocketClient(url, new ClientConfig());
        Thread.sleep(5000 * 1000);
    }
}
