package com.broheim.websocket.server;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.spring.annonation.Command;
import com.broheim.websocket.spring.annonation.EnableWebSocketServer;
import com.broheim.websocket.spring.annonation.WebSocketController;
import com.broheim.websocket.spring.reactor.CommandReactor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableWebSocketServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandReactor commandReactor(){
        return new CommandReactor();
    }

    @Component
    @WebSocketController("/ws")
    public static class EndpointController {
        @Command("hello")
        public void doHandle(ChannelContext channelContext, String message) {
            System.out.println(message);
        }
    }
}
