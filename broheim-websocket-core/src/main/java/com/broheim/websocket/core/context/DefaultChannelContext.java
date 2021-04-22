package com.broheim.websocket.core.context;

import com.broheim.websocket.core.endpoint.WebSocketEndpoint;
import com.broheim.websocket.core.event.RequestResponseMessageEvent;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.event.SendMessageEvent;
import com.broheim.websocket.core.event.SendSyncMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.handler.Handler;
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Getter
@Setter
@Slf4j
public class DefaultChannelContext implements ChannelContext {

    private WebSocketEndpoint endpoint;

    private String message;

    private Throwable exception;

    private CloseReason closeReason;

    private Protocol<SimpleMessage> protocol = new SimpleProtocol();

    private List<Handler> handlerList;

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
    public List<Handler> getHandlerList() {
        return handlerList;
    }

    @Override
    public void sendMessageAsync(String message) throws MessageProtocolException {
        this.endpoint.getEventPublisher().publish(new SendAsyncMessageEvent(this, message));
    }

    @Override
    public void sendMessageAsync(String message, Long timeOut) throws MessageProtocolException, IOException {
        SendAsyncMessageEvent asyncMessageEvent = new SendAsyncMessageEvent(this, message);
        asyncMessageEvent.setTimeOut(timeOut);
        this.endpoint.getEventPublisher().publish(asyncMessageEvent);
    }

    @Override
    public boolean sendMessageSync(String message) throws MessageProtocolException {
        this.endpoint.getEventPublisher().publish(new SendSyncMessageEvent(this, protocol.encode(this, message)));
        return true;
    }

    @Override
    public void sendMessage(String message, EventListener<SendMessageEvent> eventListener) throws MessageProtocolException {
        SendMessageEvent sendMessageEvent = new SendSyncMessageEvent(this, protocol.encode(this, message));
        if (null != eventListener) {
            eventListener.onEvent(sendMessageEvent);
        }
    }

    @Override
    public Object sendMessage(String message) throws MessageProtocolException, ExecutionException, InterruptedException {
        RequestResponseMessageEvent requestResponseMessageEvent = new RequestResponseMessageEvent(this, message);
        Future future = this.endpoint.getEventPublisher().publish(requestResponseMessageEvent);
        future.get();
        return requestResponseMessageEvent.getResult();
    }

    @Override
    public void sendText(String text) throws IOException {
        this.endpoint.getEventPublisher().publish(new SendSyncMessageEvent(this, text));
    }
}
