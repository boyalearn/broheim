package com.broheim.websocket.core.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;

public interface RunnableHandler extends Handler{

    void handle(ChannelContext channelContext, String message);
}
