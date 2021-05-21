package com.broheim.websocket.core.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;

public interface CallableHandler extends Handler {

    Object handle(ChannelContext channelContext, String message);
}
