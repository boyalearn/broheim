package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.CloseEvent;
import com.broheim.websocket.core.event.ConnectionEvent;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
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
public class ClientHeartbeatListener implements EventListener<Event> {

    private static ScheduledExecutorService HEART_POOL = Executors.newScheduledThreadPool(1);

    private static final String PING = "ping";

    private static final String ACK = "ack";

    private long delay = 2;

    private Map<ChannelContext, Future> futureHolder = new ConcurrentHashMap();

    private Map<ChannelContext, AtomicInteger> loseTimeHolder = new ConcurrentHashMap();


    public SimpleProtocol simpleProtocol;

    @Override
    public Object onEvent(Event event) throws Exception {
        if (event instanceof ConnectionEvent) {
            ChannelContext channelContext = ((ConnectionEvent) event).getChannelContext();
            startHeartBeat(channelContext);
            return null;
        }

        if (event instanceof OnMessageEvent) {
            OnMessageEvent onMessageEvent = (OnMessageEvent) event;
            ChannelContext channelContext = onMessageEvent.getChannelContext();
            responsePingRequest((DefaultChannelContext) channelContext, onMessageEvent.getMessage());
            return null;
        }

        if (event instanceof CloseEvent) {
            ChannelContext channelContext = ((CloseEvent) event).getChannelContext();
            stopHeartBeat(channelContext);
        }
        return null;
    }

    private void responsePingRequest(DefaultChannelContext channelContext, String message) {
        try {
            loseTimeHolder.get(channelContext).set(0);
            SimpleMessage acceptMessage = simpleProtocol.decode(message);
            if (PING.equals(acceptMessage.getType())) {
                channelContext.sendText(simpleProtocol.encode(ACK));
            }
        } catch (Exception e) {
            log.error("response ping error", e);
        }
    }

    public void startHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        ScheduledFuture<?> future = HEART_POOL.scheduleWithFixedDelay(new ClientHeartbeatWorker(context, loseTimeHolder), this.delay, this.delay, TimeUnit.SECONDS);
        futureHolder.put(channelContext, future);
        loseTimeHolder.put(channelContext, new AtomicInteger(0));
    }

    public void stopHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        Future future = futureHolder.get(context);
        if (null != future) {
            future.cancel(true);
            futureHolder.remove(context);
        }
        loseTimeHolder.remove(channelContext);
    }

    public class ClientHeartbeatWorker implements Runnable {

        private ChannelContext channelContext;

        private Map<ChannelContext, AtomicInteger> loseTimeHolder;

        public ClientHeartbeatWorker(ChannelContext channelContext, Map<ChannelContext, AtomicInteger> loseTimeHolder) {
            this.channelContext = channelContext;
            this.loseTimeHolder = loseTimeHolder;
        }

        @Override
        public void run() {
            AtomicInteger loseTime = this.loseTimeHolder.get(this.channelContext);
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
