package com.broheim.websocket.core.context;

import com.broheim.websocket.core.endpoint.WebSocketEndpoint;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.event.SendMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import java.io.IOException;

@Getter
@Setter
@Slf4j
public class DefaultChannelContext implements ChannelContext {

    private WebSocketEndpoint endpoint;

    private String message;

    private Throwable exception;

    private CloseReason closeReason;

    private Protocol<SimpleMessage> protocol = new SimpleProtocol();

    private EndpointConfig config;

    public DefaultChannelContext(WebSocketEndpoint endpoint, Throwable exception) {
        this.endpoint = endpoint;
        this.exception = exception;
    }

    public DefaultChannelContext(WebSocketEndpoint endpoint, EndpointConfig config) {
        this.endpoint = endpoint;
        this.config = config;
    }

    public DefaultChannelContext(WebSocketEndpoint endpoint, String message) {
        this.endpoint = endpoint;
        this.message = message;
    }

    public DefaultChannelContext(WebSocketEndpoint endpoint, CloseReason closeReason) {
        this.endpoint = endpoint;
        this.closeReason = closeReason;
    }


    @Override
    public void sendMessageAsync(String message) throws MessageProtocolException {
        this.endpoint.getEventPublisher().publish(new SendAsyncMessageEvent(this, protocol.encode(this, message)));
    }

    @Override
    public boolean sendMessageSync(String message) throws MessageProtocolException {
        this.endpoint.getEventPublisher().publish(new SendAsyncMessageEvent(this, protocol.encode(this, message)));
        return true;
    }

    @Override
    public void sendMessage(String message, EventListener<SendMessageEvent> eventListener) throws MessageProtocolException {
        SendMessageEvent sendMessageEvent = new SendAsyncMessageEvent(this, protocol.encode(this, message));
        if (null != eventListener) {
            eventListener.onEvent(sendMessageEvent);
        }
    }

    @Override
    public void sendText(String text) throws IOException {
        this.endpoint.getEventPublisher().publish(new SendAsyncMessageEvent(this, text));
    }
}
