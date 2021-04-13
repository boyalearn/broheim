package com.broheim.websocket.core.handler;


import com.broheim.websocket.core.context.ChannelContext;

public interface Handler {

    void handle(ChannelContext channelContext, String message);
}
