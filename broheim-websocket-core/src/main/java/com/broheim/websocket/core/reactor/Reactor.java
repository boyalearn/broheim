package com.broheim.websocket.core.reactor;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.handler.Handler;

import java.util.List;

/**
 * 分发业务层的逻辑处理。如果不想分发只需要实现dispatch方法
 */
public interface Reactor {

    void addHandler(Handler handler);

    void addHandlers(List<Handler> handlers);

    void dispatch(String message, ChannelContext context);
}
