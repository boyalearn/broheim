package com.broheim.websocket.core.endpoint.client;

import com.broheim.websocket.core.endpoint.ClientWebSocketEndpoint;
import com.broheim.websocket.core.endpoint.EndpointConfig;
import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.accept.OnCloseEvent;
import com.broheim.websocket.core.listener.AsyncMessageSendListener;
import com.broheim.websocket.core.listener.ClientHeartbeatListener;
import com.broheim.websocket.core.listener.Listener;
import com.broheim.websocket.core.listener.RequestResponseMessageSendListener;
import com.broheim.websocket.core.listener.SyncMessageSendListener;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

@Slf4j
public class WebSocketClient extends EndpointConfig implements Listener, ChannelContext {

    private static EventPublisher completePublisher;

    private String url;

    private Class<?> clientClass;

    private volatile ChannelContext channelContext;

    public void connect(String url, Class<?> clientClass) throws IOException, DeploymentException {

        this.url = url;
        this.clientClass = clientClass;

        if (null == this.listeners) {
            this.listeners = new ArrayList<>();
            this.listeners.add(new ClientHeartbeatListener());
            this.listeners.add(new SyncMessageSendListener());
            this.listeners.add(new AsyncMessageSendListener());
            this.listeners.add(new RequestResponseMessageSendListener());
            this.listeners.add(this);
        }
        defaultConfig();
        completePublisher = this.eventPublisher;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = URI.create(url);
        Session session = container.connectToServer(clientClass, uri);
        this.channelContext = (ChannelContext) session.getUserProperties().get(ClientWebSocketEndpoint.CHANNEL_CONTEXT);
        System.out.println(this.channelContext);
    }

    public static EventPublisher getConfigPublisher() {
        return completePublisher;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event instanceof OnCloseEvent) {
            OnCloseEvent onCloseEvent = (OnCloseEvent) event;
            log.debug("reconnect to server");
            try {
                connect(this.url, this.clientClass);
            } catch (Exception e) {
                onEvent(onCloseEvent);
            }
        }
    }

    @Override
    public void sendMessageAsync(String message) throws Exception {
        this.channelContext.sendMessageAsync(message);
    }

    @Override
    public boolean sendMessageSync(String message) throws Exception {
        return this.channelContext.sendMessageSync(message);
    }

    @Override
    public boolean sendMessageSync(String message, Long timeOut) throws Exception {
        return this.channelContext.sendMessageSync(message, timeOut);
    }

    @Override
    public Object sendMessage(String message) throws Exception {
        return this.channelContext.sendMessage(message);
    }

    @Override
    public Object sendMessage(String message, Long timeOut) throws Exception {
        return this.channelContext.sendMessage(message, timeOut);
    }

    @Override
    public void sendText(String text) throws Exception {
        this.channelContext.sendText(text);
    }
}
