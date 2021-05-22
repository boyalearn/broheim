package com.broheim.websocket.spring.handler;


import com.broheim.websocket.core.endpoint.context.ChannelContext;

public interface CommandRunnableHandler {

    String getCmd();

    void handle(ChannelContext channelContext, String body);
}

