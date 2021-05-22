package com.broheim.websocket.core.event.send;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.event.accept.AbstractEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SendMessageEvent extends AbstractEvent {

    private String message;

    private volatile Object Result;

    private volatile Exception exception;

    public SendMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext);
        this.message = message;
    }
}
