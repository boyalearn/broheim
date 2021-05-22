package com.broheim.websocket.client;

import com.broheim.websocket.core.endpoint.ClientWebSocketEndpoint;
import com.broheim.websocket.core.endpoint.client.WebSocketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientTest {
    public static void main(String[] args) throws Exception {
        String url = "ws://localhost:8000/ws";
        WebSocketClient client = new WebSocketClient();
        client.connect(url, ClientWebSocketEndpoint.class);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "121212");
        data.put("cmd", "hello");
        ObjectMapper objectMapper = new ObjectMapper();
        for (; ; ) {
            Object o = client.sendMessage(objectMapper.writeValueAsString(data));
            log.info("response is {}", o);
            Thread.sleep(2000);
        }
    }

}
