package com.broheim.websocket.core.acceptor;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.message.Message;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.reactor.Reactor;
import com.broheim.websocket.core.thread.NamedThreadFactory;
import com.broheim.websocket.core.thread.Worker;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultAcceptor implements Acceptor {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            4, 8, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5000),
            new NamedThreadFactory("websocket-reactor-pool-"), new ThreadPoolExecutor.AbortPolicy());

    private Reactor reactor;

    private Protocol<Message> protocol;

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
        EXECUTOR.execute(new Worker(this.reactor, this.protocol, channelContext));
    }
}
