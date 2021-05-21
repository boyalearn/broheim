package com.broheim.websocket.core.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;

public class DefaultHandler implements RunnableHandler {
    @Override
    public void handle(ChannelContext channelContext, String message) {
        System.out.println(message);
    }
}
