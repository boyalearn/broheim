package com.broheim.websocket.core.event;


import com.broheim.websocket.core.context.ChannelContext;

public class ConnectionEvent extends AbstractEvent{

    public ConnectionEvent(ChannelContext channelContext) {
        super(channelContext);
    }
}
