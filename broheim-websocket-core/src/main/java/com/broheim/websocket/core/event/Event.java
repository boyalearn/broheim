package com.broheim.websocket.core.event;


import com.broheim.websocket.core.context.ChannelContext;

public interface Event {

    ChannelContext getChannelContext();
}
