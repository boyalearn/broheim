package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.listener.EventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Worker<E> implements Runnable {

    private List<EventListener> listeners;

    private Event event;

    public Worker(List<EventListener> listeners, Event event) {
        this.listeners = listeners;
        this.event = event;

    }

    @Override
    public void run() {
        for (EventListener listener : this.listeners) {
            listener.onEvent(this.event);
        }
    }
}
