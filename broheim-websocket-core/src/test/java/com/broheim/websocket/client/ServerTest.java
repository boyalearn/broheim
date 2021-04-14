package com.broheim.websocket.client;

import com.broheim.websocket.core.endpoint.AbstractWebSocketEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;

@SpringBootApplication
public class ServerTest {
    public static void main(String[] args) {
        SpringApplication.run(ServerTest.class, args);
    }


    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Component
    @ServerEndpoint("/ws")
    public static class TestEndpoint extends AbstractWebSocketEndpoint {

    }
}
