package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.accept.OnCloseEvent;
import com.broheim.websocket.core.event.accept.OnConnectionEvent;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ClientHeartbeatListener implements Listener {

    private static ScheduledExecutorService HEART_POOL = Executors.newScheduledThreadPool(4);

    private static final String PING = "ping";

    private static final String ACK = "pong";

    private long delay = 2;

    private Map<ChannelContext, Future> futureHolder = new ConcurrentHashMap();

    private Map<ChannelContext, AtomicInteger> loseTimeHolder = new ConcurrentHashMap();


    public SimpleProtocol simpleProtocol = new SimpleProtocol();

    @Override
    public void onEvent(Event event) throws Exception {
        if (event instanceof OnConnectionEvent) {
            ChannelContext channelContext = ((OnConnectionEvent) event).getChannelContext();
            startHeartBeat(channelContext);
        }

        if (event instanceof OnMessageEvent) {
            OnMessageEvent onMessageEvent = (OnMessageEvent) event;
            DefaultChannelContext channelContext = (DefaultChannelContext) onMessageEvent.getChannelContext();
            try {
                SimpleMessage acceptMessage = simpleProtocol.decode(onMessageEvent.getMessage());
                if (PING.equals(acceptMessage.getType())) {
                    acceptMessage.setType(ACK);
                    channelContext.sendText(simpleProtocol.encode(acceptMessage));
                }
            } catch (Exception e) {
                log.error("message parse error", e);
            }
            loseTimeHolder.get(channelContext).set(0);
        }

        if (event instanceof OnCloseEvent) {
            ChannelContext channelContext = ((OnCloseEvent) event).getChannelContext();
            stopHeartBeat(channelContext);
        }
    }

    public void startHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        ScheduledFuture<?> future = HEART_POOL.scheduleWithFixedDelay(new ClientHeartbeatWorker(context, loseTimeHolder, this.simpleProtocol), this.delay, this.delay, TimeUnit.SECONDS);
        futureHolder.put(channelContext, future);
        loseTimeHolder.put(channelContext, new AtomicInteger(0));
    }

    public void stopHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        Future future = futureHolder.remove(context);
        if (null != future) {
            future.cancel(true);
        }else {
            log.error("future is null");
        }
        loseTimeHolder.remove(channelContext);
    }

    public static class ClientHeartbeatWorker implements Runnable {

        private final static SimpleMessage PING_DATA = new SimpleMessage();


        private ChannelContext channelContext;

        private Map<ChannelContext, AtomicInteger> loseTimeHolder;

        private SimpleProtocol simpleProtocol;

        public ClientHeartbeatWorker(ChannelContext channelContext, Map<ChannelContext, AtomicInteger> loseTimeHolder, SimpleProtocol simpleProtocol) {
            this.channelContext = channelContext;
            this.loseTimeHolder = loseTimeHolder;
            this.simpleProtocol = simpleProtocol;
        }

        @Override
        public void run() {
            AtomicInteger loseTime = this.loseTimeHolder.get(this.channelContext);
            log.debug("ping and check connection...");
            try {
                channelContext.sendText(simpleProtocol.encode(PING_DATA));
            } catch (Exception e) {
                log.error("ping error", e);
            }
            log.debug("lose time is {},check connect...", loseTime.get());
            if (loseTime.get() >= 3) {
                try {
                    DefaultChannelContext defaultChannelContext = (DefaultChannelContext) channelContext;
                    defaultChannelContext.getSession().close();
                } catch (IOException e) {
                    log.error("client close connect error...", e);

                } finally {
                    log.debug("client close connect...");
                }
            }
            loseTime.incrementAndGet();
        }
    }
}
