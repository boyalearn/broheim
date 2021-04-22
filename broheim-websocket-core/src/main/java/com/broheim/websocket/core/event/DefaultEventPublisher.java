package com.broheim.websocket.core.event;


import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.thread.NamedThreadFactory;
import com.broheim.websocket.core.thread.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultEventPublisher implements EventPublisher {

    List<EventListener> listeners = new ArrayList();

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            4, 8, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5000),
            new NamedThreadFactory("websocket-reactor-pool-"), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void publish(Event event) {
        EXECUTOR.execute(new Worker<>(listeners, event));
    }

    @Override
    public void addListeners(List<EventListener> listeners) {
        if (null != this.listeners) {
            this.listeners.addAll(listeners);
        }
        this.listeners = listeners;
    }
}
