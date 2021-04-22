package com.broheim.websocket.core.event;


import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.listener.EventListener;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 事件发布者
 */
public interface EventPublisher {

    Future publish(Event e);

    void addListeners(List<EventListener> listeners);

    void addHandlerList(List<Handler> handlerList);

    List<Handler> getHandlerList();
}
