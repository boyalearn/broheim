package com.broheim.websocket.core.listener;

public interface EventListener<Event> {
    void onEvent(Event event);
}
