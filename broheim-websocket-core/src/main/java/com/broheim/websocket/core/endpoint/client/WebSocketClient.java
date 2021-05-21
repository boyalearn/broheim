package com.broheim.websocket.core.endpoint.client;

import com.broheim.websocket.core.endpoint.WebSocketEndpoint;
import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.CloseEvent;
import com.broheim.websocket.core.event.ConnectionEvent;
import com.broheim.websocket.core.event.ErrorEvent;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

@Slf4j
@ClientEndpoint
public class WebSocketClient implements WebSocketEndpoint {

    private static String URL;

    private static EventPublisher eventPublisher;

    private ChannelContext channelContext;

    /**
     * 必须保留。新连接创建的时候需要触发此构造函数
     */
    public WebSocketClient() {
    }

    public WebSocketClient(String url, EventPublisher eventPublisher) {
        URL = url;
        this.eventPublisher = eventPublisher;
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = URI.create(URL);
            container.connectToServer(WebSocketClient.class, uri);
        } catch (DeploymentException | IOException e) {
            log.error("connection exception", e);
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        log.debug("client connection...");
        this.channelContext = new DefaultChannelContext(session, eventPublisher);
        this.eventPublisher.publish(new ConnectionEvent(this.channelContext, endpointConfig));
    }

    @OnMessage
    public void onMessage(String message) {
        log.debug("Client onMessage: " + message);
        this.eventPublisher.publish(new OnMessageEvent(this.channelContext, message));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("on error happen. session id is {} ...", session.getId(), error);
        this.eventPublisher.publish(new ErrorEvent(this.channelContext, error));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("on close happen. session id is {} ...", session.getId());
        this.eventPublisher.publish(new CloseEvent(this.channelContext, closeReason));
        reconnect();
    }

    private void reconnect() {
        log.debug("start reconnect");
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = URI.create(URL);
            container.connectToServer(WebSocketClient.class, uri);
        } catch (DeploymentException | IOException e) {
            log.error("connection exception", e);
            reconnect();
        } finally {

        }
    }

}
