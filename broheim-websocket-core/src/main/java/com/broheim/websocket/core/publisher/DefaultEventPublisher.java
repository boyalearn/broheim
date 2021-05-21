package com.broheim.websocket.core.publisher;


import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.publisher.threadpool.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultEventPublisher implements EventPublisher {

    List<EventListener> listeners;


    public DefaultEventPublisher(List<EventListener> listeners) {
        this.listeners = listeners;
    }

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            20,
            40,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5000),
            new NamedThreadFactory("websocket-pool-"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Override
    public Future publish(Event event) {
        if (null == this.listeners) {
            return null;
        }
        return EXECUTOR.submit(new Worker(listeners, event));
    }

    @Slf4j
    public static class Worker implements Callable<Object> {

        private List<EventListener> listeners;

        private Event event;

        public Worker(List<EventListener> listeners, Event event) {
            this.listeners = listeners;
            this.event = event;

        }

        private boolean isMatchActualTypeArgument(EventListener listener, Event event) {
            Type[] actualTypeArguments = listener.getClass().getGenericInterfaces();
            if (actualTypeArguments == null && actualTypeArguments.length == 0) {
                return true;
            }
            actualTypeArguments = ((ParameterizedType) actualTypeArguments[0]).getActualTypeArguments();
            if (actualTypeArguments == null && actualTypeArguments.length == 0) {
                return true;
            }
            return ((Class<?>) actualTypeArguments[0]).isAssignableFrom(event.getClass());
        }

        @Override
        public Object call() throws Exception {
            for (EventListener listener : this.listeners) {
                if (isMatchActualTypeArgument(listener, event)) {
                    try {
                        listener.onEvent(this.event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
