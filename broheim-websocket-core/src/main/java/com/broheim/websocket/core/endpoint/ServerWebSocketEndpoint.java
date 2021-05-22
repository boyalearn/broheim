package com.broheim.websocket.core.endpoint;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.endpoint.server.WebSocketServer;
import com.broheim.websocket.core.event.accept.OnCloseEvent;
import com.broheim.websocket.core.event.accept.OnConnectionEvent;
import com.broheim.websocket.core.event.accept.OnErrorEvent;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@Slf4j
public abstract class ServerWebSocketEndpoint implements WebSocketEndpoint {

    private EventPublisher publisher;

    private ChannelContext channelContext;

    public ServerWebSocketEndpoint() {
        this.publisher = WebSocketServer.findEventPublisher(this.getClass());
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.debug("session id {} connect...", session.getId());
        this.channelContext = new DefaultChannelContext(session, this.publisher);
        publisher.publish(new OnConnectionEvent(this.channelContext, config));
    }


    @OnMessage
    public void onMessage(Session session, String message) {
        log.debug("session {} on message {}...", session.getId(), message);
        publisher.publish(new OnMessageEvent(this.channelContext, message));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.debug("session id {} on error...", session.getId(), error);
        publisher.publish(new OnErrorEvent(this.channelContext, error));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.debug("session id {} close...", session.getId());
        publisher.publish(new OnCloseEvent(this.channelContext, closeReason));
    }
}