package com.broheim.websocket.server;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.server.WebSocketServer;
import com.broheim.websocket.core.handler.CallableHandler;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.handler.RunnableHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ServerTest {
    public static void main(String[] args) {
        WebSocketServer server = new WebSocketServer();
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new ServerHandler());
        handlers.add(new ServerCallableHandler());
        server.setHandlers(handlers);
        SpringApplication.run(ServerTest.class, args);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    public static class ServerHandler implements RunnableHandler {
        @Override
        public void handle(ChannelContext channelContext, String message) {
            try {
                Object result = channelContext.sendMessage(message);
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ServerCallableHandler implements CallableHandler {

        @Override
        public Object handle(ChannelContext channelContext, String message) {
            try {
                Object result = channelContext.sendMessage(message + "1");
                System.out.println(result);
                return message;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

}
