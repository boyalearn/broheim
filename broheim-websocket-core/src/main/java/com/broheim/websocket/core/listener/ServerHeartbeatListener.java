package com.broheim.websocket.core.listener;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.context.DefaultChannelContext;
import com.broheim.websocket.core.event.CloseEvent;
import com.broheim.websocket.core.event.ConnectionEvent;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.thread.ServerHeartbeatWorker;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerHeartbeatListener<Event> implements EventListener<Event> {

    private long delay = 2;

    private static ScheduledExecutorService HEART_POOL = Executors.newScheduledThreadPool(10);

    private Map<Session, Future> runnableMap = new ConcurrentHashMap<>();

    @Override
    public void onEvent(Event event) {
        if (event instanceof ConnectionEvent) {
            ChannelContext channelContext = ((ConnectionEvent) event).getChannelContext();

            startHeartBeat(channelContext);
        }

        if (event instanceof OnMessageEvent) {
            ChannelContext channelContext = ((OnMessageEvent) event).getChannelContext();
            String message = channelContext.getMessage();
            try {
                SimpleMessage acceptMessage = (SimpleMessage) channelContext.getProtocol().decode(message);
                if (Protocol.PING.equals(acceptMessage.getCmd())) {
                    channelContext.sendMessageSync(channelContext.getProtocol().encode(channelContext, Protocol.ACK));
                }
            } catch (MessageProtocolException e) {
                log.error("message parse error", e);
            }
        }

        if (event instanceof CloseEvent) {
            ChannelContext channelContext = ((CloseEvent) event).getChannelContext();
            stopHeartBeat(channelContext);
        }
    }

    public void startHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        Session session = context.getEndpoint().getSession();
        ScheduledFuture<?> future = HEART_POOL.scheduleWithFixedDelay(new ServerHeartbeatWorker(context), this.delay, this.delay, TimeUnit.SECONDS);
        runnableMap.put(session, future);
    }

    public void stopHeartBeat(ChannelContext channelContext) {
        DefaultChannelContext context = (DefaultChannelContext) channelContext;
        Session session = context.getEndpoint().getSession();
        Future future = runnableMap.get(session);
        if (null != future) {
            future.cancel(true);
            runnableMap.remove(session);
        }

    }
}
