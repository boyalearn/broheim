package com.broheim.websocket.core.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;

public class DefaultCallableHandler implements CallableHandler {
    @Override
    public Object handle(ChannelContext channelContext, String message) {
        return message;
    }
}
