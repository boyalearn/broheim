package com.broheim.websocket.core.endpoint.context;

import com.broheim.websocket.core.event.send.RequestResponseMessageEvent;
import com.broheim.websocket.core.event.send.SendAsyncMessageEvent;
import com.broheim.websocket.core.event.send.SendSyncMessageEvent;
import com.broheim.websocket.core.exception.ChannelCloseException;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;

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
    public boolean sendMessageSync(String message) throws Exception {
        SendSyncMessageEvent sendSyncMessageEvent = new SendSyncMessageEvent(this, message);
        this.eventPublisher.publish(sendSyncMessageEvent);
        if (null != sendSyncMessageEvent.getException()) {
            throw sendSyncMessageEvent.getException();
        }
        return (boolean) sendSyncMessageEvent.getResult();
    }

    @Override
    public boolean sendMessageSync(String message, Long timeOut) throws Exception {
        SendSyncMessageEvent sendSyncMessageEvent = new SendSyncMessageEvent(this, message);
        sendSyncMessageEvent.setTimeOut(timeOut);
        this.eventPublisher.publish(sendSyncMessageEvent);
        if (null != sendSyncMessageEvent.getException()) {
            throw sendSyncMessageEvent.getException();
        }
        return (boolean) sendSyncMessageEvent.getResult();
    }

    @Override
    public Object sendMessage(String message) throws Exception {
        RequestResponseMessageEvent requestResponseMessageEvent = new RequestResponseMessageEvent(this, message);
        this.eventPublisher.publish(requestResponseMessageEvent);
        if (null != requestResponseMessageEvent.getException()) {
            throw requestResponseMessageEvent.getException();
        }
        return requestResponseMessageEvent.getResult();
    }

    @Override
    public Object sendMessage(String message, Long timeOut) throws Exception {
        RequestResponseMessageEvent requestResponseMessageEvent = new RequestResponseMessageEvent(this, message);
        requestResponseMessageEvent.setTimeOut(timeOut);
        this.eventPublisher.publish(requestResponseMessageEvent);
        if (null != requestResponseMessageEvent.getException()) {
            throw requestResponseMessageEvent.getException();
        }
        return requestResponseMessageEvent.getResult();
    }

    @Override
    public void sendText(String text) throws Exception {
        synchronized (this.session) {
            if (this.session.isOpen()) {
                this.session.getBasicRemote().sendText(text);
                return;
            }
            throw new ChannelCloseException("channel is close.");
        }
    }
}
