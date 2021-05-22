package com.broheim.websocket.core.event.accept;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.event.send.SendEvent;

public abstract class AbstractEvent implements SendEvent {

    private ChannelContext channelContext;


    public AbstractEvent(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    public ChannelContext getChannelContext() {
        return channelContext;
    }
}
