package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.event.accept.Event;

import java.util.List;

public class ListenerChain {

    private int position = 0;

    private Listener[] listeners;

    public ListenerChain(List<Listener> listenerList) {
        this.listeners = (Listener[]) listenerList.toArray();
    }

    public Object doChain(Event event) throws Exception {
        return null;
    }
}
