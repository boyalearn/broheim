package com.broheim.websocket.core.listener;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.accept.OnCloseEvent;
import com.broheim.websocket.core.event.accept.OnConnectionEvent;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ServerHeartbeatListener implements Listener {

    private static final String PING = "ping";

    private static final String PONG = "pong";

    private SimpleProtocol simpleProtocol;

    private long delay = 2;

    public ServerHeartbeatListener() {
        this.simpleProtocol = new SimpleProtocol();
    }

    public ServerHeartbeatListener(SimpleProtocol simpleProtocol) {
        this.simpleProtocol = simpleProtocol;
    }

    private static ScheduledExecutorService HEART_POOL = Executors.newScheduledThreadPool(10);

    private Map<ChannelContext, HeartbeatContext> heartbeatContextHolder = new ConcurrentHashMap<>();

    @Override
    public void onEvent(Event event) throws Exception {

        if (event instanceof OnConnectionEvent) {
            ChannelContext channelContext = ((OnConnectionEvent) event).getChannelContext();
            startHeartBeat(channelContext);
        }

        if (event instanceof OnMessageEvent) {
            OnMessageEvent onMessageEvent = (OnMessageEvent) event;
            DefaultChannelContext defaultChannelContext = (DefaultChannelContext) onMessageEvent.getChannelContext();
            String message = ((OnMessageEvent) event).getMessage();
            HeartbeatContext heartbeatContext = heartbeatContextHolder.get(defaultChannelContext);
            heartbeatContext.getLastAcceptTime().set(new Date().getTime());
            try {
                SimpleMessage acceptMessage = simpleProtocol.decode(message);
                if (PING.equals(acceptMessage.getType())) {
                    acceptMessage.setType(PONG);
                    defaultChannelContext.sendText(simpleProtocol.encode(acceptMessage));
                }
            } catch (MessageProtocolException e) {
                log.error("message parse error", e);
            }
            heartbeatContext.getLoseTime().set(0);
        }

        if (event instanceof OnCloseEvent) {
            ChannelContext channelContext = ((OnCloseEvent) event).getChannelContext();
            stopHeartBeat(channelContext);
        }
    }

    public void startHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        ScheduledFuture<?> future = HEART_POOL.scheduleWithFixedDelay(new ServerHeartbeatWorker(context, simpleProtocol, heartbeatContextHolder), this.delay, this.delay, TimeUnit.SECONDS);
        HeartbeatContext heartbeatContext = new HeartbeatContext();
        heartbeatContext.setFuture(future);
        heartbeatContextHolder.put(context, heartbeatContext);
    }

    public void stopHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        HeartbeatContext heartbeatContext = heartbeatContextHolder.remove(context);
        if (null != heartbeatContext) {
            heartbeatContext.getFuture().cancel(true);
        }

    }

    @Slf4j
    private static class ServerHeartbeatWorker implements Runnable {

        private final static SimpleMessage PING_DATA = new SimpleMessage();

        private Map<ChannelContext, HeartbeatContext> heartbeatContextHolder;

        static {
            PING_DATA.setType(PING);
        }

        private ChannelContext channelContext;

        private SimpleProtocol simpleProtocol;

        public ServerHeartbeatWorker(ChannelContext channelContext, SimpleProtocol simpleProtocol, Map<ChannelContext, HeartbeatContext> heartbeatContextHolder) {
            this.channelContext = channelContext;
            this.simpleProtocol = simpleProtocol;
            this.heartbeatContextHolder = heartbeatContextHolder;
        }


        @Override
        public void run() {
            log.debug("ping and check connection...");
            try {
                channelContext.sendText(simpleProtocol.encode(PING_DATA));
            } catch (Exception e) {
                log.error("ping error", e);
            }

            DefaultChannelContext defaultChannelContext = (DefaultChannelContext) this.channelContext;
            HeartbeatContext heartbeatContext = heartbeatContextHolder.get(defaultChannelContext);
            log.debug("server close connect time {}", heartbeatContext.getLoseTime());
            if (heartbeatContext.getLoseTime().get() > 4) {
                try {
                    ((DefaultChannelContext) channelContext).getSession().close();
                    return;
                } catch (IOException e) {
                    log.error("server close connect error...", e);
                } finally {
                    log.debug("server close connect...");
                }
            }
            if (new Date().getTime() - heartbeatContext.getLastAcceptTime().get() > 3 * 1000) {
                heartbeatContext.getLoseTime().incrementAndGet();
            }
        }
    }

    @Getter
    @Setter
    private static class HeartbeatContext {

        private Future future;

        private AtomicInteger loseTime = new AtomicInteger(0);

        private AtomicLong lastAcceptTime = new AtomicLong(0);
    }
}
