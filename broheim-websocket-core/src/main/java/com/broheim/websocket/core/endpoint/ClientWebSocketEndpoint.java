package com.broheim.websocket.core.endpoint;

import com.broheim.websocket.core.endpoint.client.WebSocketClient;
import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.accept.OnCloseEvent;
import com.broheim.websocket.core.event.accept.OnConnectionEvent;
import com.broheim.websocket.core.event.accept.OnErrorEvent;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@Slf4j
@ClientEndpoint
public class ClientWebSocketEndpoint implements WebSocketEndpoint {

    public final static String CHANNEL_CONTEXT = "channelContext";

    private EventPublisher eventPublisher;

    private ChannelContext channelContext;

    /**
     * 必须保留。新连接创建的时候需要触发此构造函数
     */
    public ClientWebSocketEndpoint() {
        this.eventPublisher = WebSocketClient.getConfigPublisher();
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        log.debug("client connection...");
        this.channelContext = new DefaultChannelContext(session, eventPublisher);
        session.getUserProperties().put(CHANNEL_CONTEXT, channelContext);
        this.eventPublisher.publish(new OnConnectionEvent(this.channelContext, endpointConfig));
    }

    @OnMessage
    public void onMessage(String message) {
        log.debug("Client onMessage: " + message);
        this.eventPublisher.publish(new OnMessageEvent(this.channelContext, message));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("on error happen. session id is {} ...", session.getId(), error);
        this.eventPublisher.publish(new OnErrorEvent(this.channelContext, error));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.error("on close happen. session id is {} ...", session.getId());
        this.eventPublisher.publish(new OnCloseEvent(this.channelContext, closeReason));
    }
}
