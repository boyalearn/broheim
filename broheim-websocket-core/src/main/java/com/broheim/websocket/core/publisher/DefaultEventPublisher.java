package com.broheim.websocket.core.publisher;


import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.send.RequestResponseMessageEvent;
import com.broheim.websocket.core.event.send.SendSyncMessageEvent;
import com.broheim.websocket.core.listener.Listener;
import com.broheim.websocket.core.publisher.threadpool.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultEventPublisher implements EventPublisher {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            20,
            40,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5000),
            new NamedThreadFactory("websocket-pool-"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    List<Listener> listeners;

    public DefaultEventPublisher(List<Listener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void publish(Event event) {
        if (event instanceof SendSyncMessageEvent || event instanceof RequestResponseMessageEvent) {
            for (Listener listener : this.listeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                EXECUTOR.execute(new RunnableWorker(listeners, event));
            } catch (Exception e) {
                log.error("publish event error.", e);
            }
        }
    }


    @Slf4j
    public static class RunnableWorker implements Runnable {

        private List<Listener> listeners;
        private Event event;

        public RunnableWorker(List<Listener> listeners, Event event) {
            this.listeners = listeners;
            this.event = event;
        }

        @Override
        public void run() {
            for (Listener listener : this.listeners) {
                try {
                    listener.onEvent(this.event);
                } catch (Exception e) {
                    log.error("have a error", e);
                }
            }
        }
    }
}
