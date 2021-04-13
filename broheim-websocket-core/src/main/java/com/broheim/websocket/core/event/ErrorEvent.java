package com.broheim.websocket.core.event;


import com.broheim.websocket.core.context.ChannelContext;

public class ErrorEvent extends AbstractEvent {
    public ErrorEvent(ChannelContext channelContext) {
        super(channelContext);
    }
}
