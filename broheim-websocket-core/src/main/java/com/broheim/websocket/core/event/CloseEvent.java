package com.broheim.websocket.core.event;


import com.broheim.websocket.core.context.ChannelContext;

public class CloseEvent extends AbstractEvent{


    public CloseEvent(ChannelContext channelContext) {
        super(channelContext);
    }
}
