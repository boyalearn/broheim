package com.broheim.websocket.core.event;


import com.broheim.websocket.core.context.ChannelContext;

public class OnMessageEvent extends AbstractEvent{
    public OnMessageEvent(ChannelContext channelContext) {
        super(channelContext);
    }
}
