package com.broheim.websocket.core.reactor;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.handler.Handler;

import java.util.List;

public class ClientDefaultReactor implements Reactor {
    @Override
    public void addHandler(Handler handler) {

    }

    @Override
    public void addHandlers(List<Handler> handlers) {

    }

    @Override
    public void dispatch(String message, ChannelContext context) {
        System.out.println(message);
    }
}
