package com.broheim.websocket.core.reactor;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.handler.Handler;

import java.util.List;

public interface Reactor {

    void addHandler(Handler handler);

    void addHandlers(List<Handler> handlers);

    void dispatch(String message, ChannelContext context);
}
