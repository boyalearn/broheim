package com.broheim.websocket.core.event;

import com.broheim.websocket.core.context.ChannelContext;
import lombok.Getter;

@Getter
public abstract class SendMessageEvent extends AbstractEvent {

    private String message;

    public SendMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext);
        this.message = message;
    }
}
