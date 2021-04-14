package com.broheim.websocket.core.endpoint;


import com.broheim.websocket.core.context.DefaultChannelContext;
import com.broheim.websocket.core.context.PublisherHolder;
import com.broheim.websocket.core.event.CloseEvent;
import com.broheim.websocket.core.event.ConnectionEvent;
import com.broheim.websocket.core.event.ErrorEvent;
import com.broheim.websocket.core.event.EventPublisher;
import com.broheim.websocket.core.event.OnMessageEvent;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class AbstractWebSocketServerEndpoint implements WebSocketServerEndpoint {

    private AtomicInteger sendId = new AtomicInteger(1);

    private EventPublisher publisher;

    private Session session;

    public AbstractWebSocketServerEndpoint() {
        this.publisher = PublisherHolder.findEventPublisher(this.getClass());
    }

    @Override
    public AtomicInteger sendId() {
        return sendId;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return publisher;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.debug("session id {} connect...", session.getId());
        this.session = session;
        publisher.publish(new ConnectionEvent(new DefaultChannelContext(this, config)));
    }


    @OnMessage
    public void onMessage(Session session, String message) {
        log.debug("session id {} on message...", session.getId());
        publisher.publish(new OnMessageEvent(new DefaultChannelContext(this, message)));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.debug("session id {} on error...", session.getId(), error);
        publisher.publish(new ErrorEvent(new DefaultChannelContext(this, error)));

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.debug("session id {} close...", session.getId());
        publisher.publish(new CloseEvent(new DefaultChannelContext(this, closeReason)));
    }
}
