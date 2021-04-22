package com.broheim.websocket.client;

import com.broheim.websocket.core.client.WebSocketClient;
import com.broheim.websocket.core.config.ClientConfig;
import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.handler.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTest {
    public static void main(String[] args) throws InterruptedException, MessageProtocolException, ExecutionException {
        String url = "ws://localhost:8000/ws";
        ClientConfig clientConfig = new ClientConfig();
        List<Handler> handlerList=new ArrayList<>();
        handlerList.add(new ClientHandler());
        clientConfig.setHandlers(handlerList);
        new WebSocketClient(url, clientConfig);
    }

    public static class ClientHandler implements Handler{
        @Override
        public void handle(ChannelContext channelContext, String message) {
        }
    }
}
