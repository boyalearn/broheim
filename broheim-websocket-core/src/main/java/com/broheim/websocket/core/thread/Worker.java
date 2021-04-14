package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.context.DefaultChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.reactor.Reactor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Worker<E> implements Runnable {

    private Reactor reactor;

    private Protocol<String> protocol;

    private ChannelContext context;

    public Worker(Reactor reactor, Protocol<String> protocol, ChannelContext context) {
        this.reactor = reactor;
        this.protocol = protocol;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            protocol.service(this.context, ((DefaultChannelContext) this.context).getMessage(), this.reactor);
        } catch (MessageProtocolException e) {
            log.error("message protocol parse error");
        }
    }
}
