package com.broheim.websocket.core.acceptor;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.reactor.Reactor;

public interface Acceptor {

    void setReactor(Reactor reactor);

    void setProtocol(Protocol protocol);

    void doAccept(ChannelContext channelContext);
}
