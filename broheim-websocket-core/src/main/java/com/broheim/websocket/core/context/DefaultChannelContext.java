package com.broheim.websocket.core.context;

import com.broheim.websocket.core.endpoint.WebSocketServerEndpoint;
import com.broheim.websocket.core.message.Message;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;

@Getter
@Setter
@Slf4j
public class DefaultChannelContext implements ChannelContext {

    private WebSocketServerEndpoint endpoint;

    private String message;

    private Throwable exception;

    private CloseReason closeReason;

    private Protocol<SimpleMessage> protocol = new SimpleProtocol();

    private EndpointConfig config;

    public DefaultChannelContext(WebSocketServerEndpoint endpoint, Throwable exception) {
        this.endpoint = endpoint;
        this.exception = exception;
    }

    public DefaultChannelContext(WebSocketServerEndpoint endpoint, EndpointConfig config) {
        this.endpoint = endpoint;
        this.config = config;
    }

    public DefaultChannelContext(WebSocketServerEndpoint endpoint, String message) {
        this.endpoint = endpoint;
        this.message = message;
    }

    public DefaultChannelContext(WebSocketServerEndpoint endpoint, CloseReason closeReason) {
        this.endpoint = endpoint;
        this.closeReason = closeReason;
    }

    @Override
    public boolean sendSyncMessage(String message) {
        synchronized (this.endpoint.getSession()) {
            long start = System.currentTimeMillis();
            try {
                int sendId = this.endpoint.sendId().getAndIncrement();
                this.endpoint.getSession().getBasicRemote().sendText(protocol.addProtocolHeader(message, sendId));
                protocol.wait(sendId, this.endpoint.getSession());
            } catch (Exception e) {
                log.error("send message exception. spend {} ms", System.currentTimeMillis() - start, e);
                return false;
            }
        }
        return true;
    }

    @Override
    public void sendAsyncMessage(String message) {
        synchronized (this.endpoint.getSession()) {
            this.endpoint.getSession().getAsyncRemote().sendText(message);
        }
    }

    @Override
    public void sendMessage(Message message) {

    }
}
