package com.broheim.websocket.core.client;

import com.broheim.websocket.core.config.ClientConfig;
import com.broheim.websocket.core.context.DefaultChannelContext;
import com.broheim.websocket.core.endpoint.WebSocketEndpoint;
import com.broheim.websocket.core.event.CloseEvent;
import com.broheim.websocket.core.event.ConnectionEvent;
import com.broheim.websocket.core.event.DefaultEventPublisher;
import com.broheim.websocket.core.event.ErrorEvent;
import com.broheim.websocket.core.event.EventPublisher;
import com.broheim.websocket.core.event.OnMessageEvent;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ClientEndpoint
public class WebSocketClient implements WebSocketEndpoint {

    private static String URL;

    private static ClientConfig CONFIG;

    private AtomicInteger sendId = new AtomicInteger(1);

    private Session session;

    private static Map<String, EventPublisher> publisherCenter = new ConcurrentHashMap();

    private EventPublisher eventPublisher;


    public WebSocketClient() {
    }

    public WebSocketClient(String url, ClientConfig config) {
        URL = url;
        CONFIG = config;
        this.eventPublisher = new DefaultEventPublisher();
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = URI.create(URL);
            container.connectToServer(WebSocketClient.class, uri);
        } catch (DeploymentException | IOException e) {
            log.error("connection exception", e);
        }
    }


    @Override
    public AtomicInteger sendId() {
        return this.sendId;
    }

    @Override
    public Session getSession() {
        return this.getSession();
    }

    @Override
    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.debug("client connection...");
        this.session = session;
        this.eventPublisher = getEventPublisher(session.getRequestURI());

        this.eventPublisher.publish(new ConnectionEvent(new DefaultChannelContext(this, (EndpointConfig) null)));
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        log.debug("Client onMessage: " + message);
        this.eventPublisher.publish(new OnMessageEvent(new DefaultChannelContext(this, message)));

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("on error happen. session id is {} ...", session.getId(), error);
        this.eventPublisher.publish(new ErrorEvent(new DefaultChannelContext(this, error)));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("on close happen. session id is {} ...", session.getId());
        this.eventPublisher.publish(new CloseEvent(new DefaultChannelContext(this, closeReason)));
    }

    private EventPublisher getEventPublisher(URI requestURI) {
        String cacheKey = requestURI.getHost() + ":" + requestURI.getPort() + requestURI.getPath();
        EventPublisher eventPublisher = publisherCenter.get(cacheKey);
        if (null == eventPublisher) {
            synchronized (publisherCenter) {
                eventPublisher = publisherCenter.get(cacheKey);
                if (null == eventPublisher) {
                    publisherCenter.put(cacheKey, CONFIG.buildClientPublisher());
                    eventPublisher = publisherCenter.get(cacheKey);
                }
                return eventPublisher;
            }
        }
        return eventPublisher;
    }

}
