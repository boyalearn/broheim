package com.broheim.websocket.core.event;


import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.thread.NamedThreadFactory;
import com.broheim.websocket.core.thread.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultEventPublisher implements EventPublisher {

    List<EventListener> listeners = new ArrayList();

    List<Handler> handlerList = new ArrayList();

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            20, 40, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5000),
            new NamedThreadFactory("websocket-reactor-pool-"), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public Future publish(Event event) {
        return EXECUTOR.submit(new Worker<>(listeners, event));
    }

    @Override
    public void addListeners(List<EventListener> listeners) {
        if (null != this.listeners) {
            this.listeners.addAll(listeners);
        }
        this.listeners = listeners;
    }

    @Override
    public void addHandlerList(List<Handler> handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public List<Handler> getHandlerList() {
        return handlerList;
    }
}
