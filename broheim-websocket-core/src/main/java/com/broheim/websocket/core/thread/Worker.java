package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.context.DefaultChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.Message;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.reactor.Reactor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Worker implements Runnable {

    private Reactor reactor;

    private Protocol<Message> protocol;

    private ChannelContext context;

    public Worker(Reactor reactor, Protocol<Message> protocol, ChannelContext context) {
        this.reactor = reactor;
        this.protocol = protocol;
        this.context = context;
    }

    @Override
    public void run() {
        String message = null;
        try {
            message = protocol.doProtocol(this.context, protocol.decode(((DefaultChannelContext) this.context).getMessage()));
        } catch (MessageProtocolException e) {
            log.error("message protocol parse error");
        }
        if (null != message) {
            reactor.dispatch(message, this.context);
        }
    }
}
