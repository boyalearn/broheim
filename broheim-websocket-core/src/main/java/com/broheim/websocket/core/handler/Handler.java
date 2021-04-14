package com.broheim.websocket.core.handler;


import com.broheim.websocket.core.context.ChannelContext;

/**
 * 抽象业务层面处理器
 */
public interface Handler {

    void handle(ChannelContext channelContext, String message);
}
