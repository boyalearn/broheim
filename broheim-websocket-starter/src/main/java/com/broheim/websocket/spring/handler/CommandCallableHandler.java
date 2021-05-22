package com.broheim.websocket.spring.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;

public interface CommandCallableHandler {

    String getCmd();

    Object handle(ChannelContext channelContext, String body);
}
