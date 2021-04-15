package com.broheim.websocket.core.acceptor;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.reactor.Reactor;
import com.broheim.websocket.core.thread.NamedThreadFactory;
import com.broheim.websocket.core.thread.Worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientDefaultAcceptor<E> implements Acceptor {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            1, 2, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000),
            new NamedThreadFactory("websocket-reactor-pool-"), new ThreadPoolExecutor.AbortPolicy());

    private Reactor reactor;

    private Protocol<E> protocol;

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
