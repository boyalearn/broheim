package com.broheim.websocket.core.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultRunnableHandler implements RunnableHandler {
    @Override
    public void handle(ChannelContext channelContext, String message) {
        log.info(message);
    }
}
