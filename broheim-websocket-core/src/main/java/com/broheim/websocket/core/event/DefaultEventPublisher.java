package com.broheim.websocket.core.event;


import com.broheim.websocket.core.listener.EventListener;

import java.util.ArrayList;
import java.util.List;

public class DefaultEventPublisher implements EventPublisher {

    List<EventListener> listeners = new ArrayList();

    @Override
    public void publish(Event e) {
        for (EventListener listener : listeners) {
            listener.onEvent(e);
        }
    }

    @Override
    public void addListeners(List<EventListener> listeners) {
        if (null != this.listeners) {
            this.listeners.addAll(listeners);
        }
        this.listeners = listeners;
    }
}
