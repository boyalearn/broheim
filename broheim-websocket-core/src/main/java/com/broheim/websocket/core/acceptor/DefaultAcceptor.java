package com.broheim.websocket.core.acceptor;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.reactor.Reactor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultAcceptor<E> implements Acceptor {

    private Reactor reactor;

    private Protocol<E> protocol;

    public DefaultAcceptor() {
    }

    @Override
    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    @Override
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void doAccept(ChannelContext channelContext) {
        try {
            protocol.service(channelContext, channelContext.getMessage(), this.reactor);
        } catch (MessageProtocolException e) {
            log.error("message protocol parse error");
        }
    }
}
