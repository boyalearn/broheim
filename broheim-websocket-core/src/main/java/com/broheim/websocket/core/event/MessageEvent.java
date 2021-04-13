package com.broheim.websocket.core.event;


import com.broheim.websocket.core.context.ChannelContext;

public class MessageEvent extends AbstractEvent{
    public MessageEvent(ChannelContext channelContext) {
        super(channelContext);
    }
}
