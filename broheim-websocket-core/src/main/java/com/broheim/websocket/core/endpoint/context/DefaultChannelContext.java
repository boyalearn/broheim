package com.broheim.websocket.core.endpoint.context;

import com.broheim.websocket.core.event.RequestResponseMessageEvent;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.event.SendMessageEvent;
import com.broheim.websocket.core.event.SendSyncMessageEvent;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.Optional;
import java.util.concurrent.Future;

@Getter
@Setter
@Slf4j
public class DefaultChannelContext implements ChannelContext {

    private Session session;

    private EventPublisher eventPublisher;


    public DefaultChannelContext(Session session, EventPublisher eventPublisher) {
        this.session = session;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void sendMessageAsync(String message) throws Exception {
        this.eventPublisher.publish(new SendAsyncMessageEvent(this, message));
    }

    @Override
    public void sendMessageAsync(String message, Long timeOut) throws Exception {
        SendAsyncMessageEvent asyncMessageEvent = new SendAsyncMessageEvent(this, message);
        this.eventPublisher.publish(asyncMessageEvent);
    }

    @Override
    public boolean sendMessageSync(String message) throws Exception {
        Future future = this.eventPublisher.publish(new SendSyncMessageEvent(this, message));
        if (Optional.ofNullable(future.get()).isPresent()) {
            return (boolean) future.get();
        }
        return false;
    }

    @Override
    public void sendMessage(String message, EventListener<SendMessageEvent> eventListener) throws Exception {
        SendMessageEvent sendMessageEvent = new SendSyncMessageEvent(this, message);
        if (null != eventListener) {
            eventListener.onEvent(sendMessageEvent);
        }
    }

    @Override
    public Object sendMessage(String message) throws Exception {
        RequestResponseMessageEvent requestResponseMessageEvent = new RequestResponseMessageEvent(this, message);
        Future future = this.eventPublisher.publish(requestResponseMessageEvent);
        return future.get();
    }

    @Override
    public void sendText(String text) throws Exception {
        synchronized (this) {
            this.session.getBasicRemote().sendText(text);
        }
    }
}
