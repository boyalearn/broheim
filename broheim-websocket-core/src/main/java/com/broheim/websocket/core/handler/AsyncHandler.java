package com.broheim.websocket.core.handler;

import com.broheim.websocket.core.context.ChannelContext;

public interface AsyncHandler extends Handler {
    default void handle(ChannelContext channelContext, String message) {
        return;
    }

    String handle(String message);
}
