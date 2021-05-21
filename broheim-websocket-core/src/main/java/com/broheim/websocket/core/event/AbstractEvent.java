package com.broheim.websocket.core.event;


import com.broheim.websocket.core.endpoint.context.ChannelContext;

public abstract class AbstractEvent implements Event {

    private ChannelContext channelContext;


    public AbstractEvent(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    public ChannelContext getChannelContext() {
        return channelContext;
    }
}
